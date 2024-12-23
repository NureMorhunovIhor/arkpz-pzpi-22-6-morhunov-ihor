package org.nure.atark.autoinsure.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.mvel2.MVEL;
import org.nure.atark.autoinsure.dto.MaintenanceDto;
import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.dto.TechnicalScoreDto;
import org.nure.atark.autoinsure.entity.*;
import org.nure.atark.autoinsure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final IncidentRepository incidentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final CarRepository carRepository;
    private final RuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final TechnicalScoreRepository technicalScoreRepository;
    private final TechnicalScoreService technicalScoreService;

    @Autowired
    public PolicyService(PolicyRepository policyRepository , IncidentRepository incidentRepository , MaintenanceRepository maintenanceRepository,
                         CarRepository carRepository, RuleRepository ruleRepository, UserRepository userRepository, TechnicalScoreService technicalScoreService, JavaMailSender mailSender, TechnicalScoreRepository technicalScoreRepository) {
        this.policyRepository = policyRepository;
        this.incidentRepository = incidentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.carRepository = carRepository;
        this.ruleRepository = ruleRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.technicalScoreRepository = technicalScoreRepository;
        this.technicalScoreService = technicalScoreService;
    }

    @PostConstruct
    public void init() {
        checkExpiringPolicies();
    }

    public List<PolicyDto> getAllPolicies() {
        List<Policy> policies = policyRepository.findAll();
        return policies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PolicyDto> getPolicyById(Integer id) {
        return policyRepository.findById(id).map(this::convertToDto);
    }

    public List<PolicyDto> getPoliciesByUserId(Integer userId) {
        List<Policy> policies = policyRepository.findByUserId(userId);
        return policies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PolicyDto createPolicy(PolicyDto policyDto) {
        Policy policy = new Policy();
        policy.setStartDate(policyDto.getStartDate());
        policy.setEndDate(policyDto.getEndDate());
        policy.setStatus("pending");

        Car car = carRepository.findById(policyDto.getCarId())
                .orElseThrow(() -> new NoSuchElementException("Car not found"));

        policy.setCar(car);

        String carType = car.getCarType().getCarTypeName();
        int carAge = car.getYear();
        int accidentCount = calculateIncidentCount(policyDto.getCarId());
        int technicalScore = calculateTechnicalScore(policyDto.getCarId());

        BigDecimal calculatedPrice = calculatePolicyPrice(carType, carAge, accidentCount, technicalScore);
        policy.setPrice(calculatedPrice);

        Policy savedPolicy = policyRepository.save(policy);
        return convertToDto(savedPolicy);
    }

    public Optional<PolicyDto> updatePolicy(Integer id, PolicyDto policyDto) {
        if (policyRepository.existsById(id)) {
            Policy policy = policyRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Policy not found"));

            policy.setStartDate(policyDto.getStartDate());
            policy.setEndDate(policyDto.getEndDate());
            policy.setStatus(policyDto.getStatus());
            policy.setPrice(policyDto.getPrice());

            Car car = new Car();
            car.setId(policyDto.getCarId());
            policy.setCar(car);

            Policy updatedPolicy = policyRepository.save(policy);
            return Optional.of(convertToDto(updatedPolicy));
        }
        return Optional.empty();
    }

    public BigDecimal calculatePolicyPrice(String carType, int carAge, int accidentCount, int technicalScore) {
        Optional<Rule> ruleOpt = ruleRepository.findByCarType(carType);
        if (ruleOpt.isEmpty()) {
            throw new IllegalArgumentException("No rule found for car type: " + carType);
        }

        Rule rule = ruleOpt.get();
        String formula = rule.getFormula();

        BigDecimal basePrice = rule.getBasePrice();
        Map<String, Object> variables = new HashMap<>();
        variables.put("basePrice", basePrice);
        variables.put("carAge", carAge);
        variables.put("accidentCount", accidentCount);


        int idk = rule.getTechnicalFactorThreshold();
        BigDecimal idk1 = rule.getTechnicalFactorMultiplier();
        if (technicalScore < idk) {
            System.out.println("Using multiplier: " + rule.getTechnicalFactorMultiplier());
            BigDecimal technicalFactor = idk1;
            variables.put("technicalFactor", technicalFactor);
        } else {
            System.out.println("Using default factor: 1");
            BigDecimal technicalFactor = BigDecimal.ONE;
            variables.put("technicalFactor", technicalFactor);
        }

        BigDecimal finalPrice;
        try {
            finalPrice = new BigDecimal(MVEL.evalToString(formula, variables));
        } catch (Exception e) {
            throw new IllegalStateException("Error evaluating formula: " + formula, e);
        }

        return finalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    private int calculateIncidentCount(Integer carId) {
        return (int) incidentRepository.findByCarId(carId).stream()
                .filter(incident -> incident.getIncidentDate() != null)
                .count();
    }
    private int calculateTechnicalScore(Integer carId) {
        List<Maintenance> maintenances = maintenanceRepository.findByCarId(carId);

        if (maintenances.isEmpty()) {
            return 5;
        }

        Optional<TechnicalScoreDto> technicalScoreDtoOptional = technicalScoreService.getLatestTechnicalScore(carId);

        double technicalCoefficient = technicalScoreDtoOptional
                .map(TechnicalScoreDto::getValue)
                .orElse(1.0);

        Maintenance lastMaintenance = maintenances.stream()
                .max(Comparator.comparing(Maintenance::getMaintenanceDate))
                .orElseThrow();

        long daysSinceMaintenance = java.time.temporal.ChronoUnit.DAYS.between(
                lastMaintenance.getMaintenanceDate(), LocalDate.now());

        int baseScore;

        if (daysSinceMaintenance < 30) {
            baseScore = 10;
        } else if (daysSinceMaintenance < 90) {
            baseScore = 7;
        } else if (daysSinceMaintenance < 180) {
            baseScore = 5;
        } else {
            baseScore = 3;
        }

        if (technicalCoefficient < 0) {
            return baseScore;
        }

        int finalScore = (int) Math.round(baseScore * 0.5 + technicalCoefficient * 0.5);
        return Math.min(finalScore, 10);
    }


    public boolean deletePolicy(Integer id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void approvePolicy(Integer id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setStatus("approved");
        policyRepository.save(policy);
    }

    public void rejectPolicy(Integer id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setStatus("rejected");
        policyRepository.save(policy);
    }

    public void checkExpiringPolicies() {
        LocalDate today = LocalDate.now();

        List<Policy> policies = policyRepository.findAll();
        for (Policy policy : policies) {
            if (policy.getEndDate() != null &&
                    !policy.getEndDate().isBefore(today) &&
                    policy.getEndDate().isBefore(today.plusDays(7))) {
                sendPolicyExpirationEmail(policy);
            }
        }
    }


    private void sendPolicyExpirationEmail(Policy policy) {
        Optional<User> userOptional = userRepository.findById(policy.getCar().getUser().getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (policy.getCar() != null) {
                policy.getCar().getUser().getFirstName();
            }

            String subject = "Your Insurance Policy is About to Expire";
            String body = "Dear " + user.getFirstName() + ",\n\n" +
                    "Your insurance policy (ID: " + policy.getId() + ") is about to expire in less than 7 days. Please take necessary actions to renew it.\n\n" +
                    "Best regards,\nAuto Insure";

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(user.getEmail());
                helper.setSubject(subject);
                helper.setText(body);

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private PolicyDto convertToDto(Policy policy) {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setId(policy.getId());
        policyDto.setStartDate(policy.getStartDate());
        policyDto.setEndDate(policy.getEndDate());
        policyDto.setStatus(policy.getStatus());
        policyDto.setPrice(policy.getPrice());

        if (policy.getCar() != null) {
            policyDto.setCarId(policy.getCar().getId());
        } else {
            policyDto.setCarId(null);
        }
        return policyDto;
    }

}
