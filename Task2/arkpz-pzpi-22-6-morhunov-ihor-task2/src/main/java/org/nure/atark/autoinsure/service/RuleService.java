package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.entity.Rule;
import org.nure.atark.autoinsure.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    @Autowired
    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public Optional<Rule> getRuleById(Integer id) {
        return ruleRepository.findById(id);
    }

    public Rule createRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    public Optional<Rule> updateRule(Integer id, Rule updatedRule) {
        return ruleRepository.findById(id).map(existingRule -> {
            existingRule.setCarType(updatedRule.getCarType());
            existingRule.setFormula(updatedRule.getFormula());
            existingRule.setTechnicalFactorThreshold(updatedRule.getTechnicalFactorThreshold());
            existingRule.setTechnicalFactorMultiplier(updatedRule.getTechnicalFactorMultiplier());
            return ruleRepository.save(existingRule);
        });
    }

    public boolean deleteRule(Integer id) {
        if (ruleRepository.existsById(id)) {
            ruleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
