package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.Policy;
import org.nure.atark.autoinsure.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    @Autowired
    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
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

    public PolicyDto createPolicy(PolicyDto policyDto) {
        Policy policy = new Policy();
        policy.setStartDate(policyDto.getStartDate());
        policy.setEndDate(policyDto.getEndDate());
        policy.setStatus(policyDto.getStatus());
        policy.setPrice(policyDto.getPrice());

        Car car = new Car();
        car.setId(policyDto.getCarId());
        policy.setCar(car);

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



    public boolean deletePolicy(Integer id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return true;
        }
        return false;
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
