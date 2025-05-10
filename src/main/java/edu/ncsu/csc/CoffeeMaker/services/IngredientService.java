package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.repositories.IngredientRepository;

/**
 * A Service Class that represents the Ingredients to obtain an
 * IngredientRepository.
 */
@Component
@Transactional
public class IngredientService extends Service<Ingredient, Long> {

    /**
     * An independent ingredientRepository.
     */
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    protected JpaRepository<Ingredient, Long> getRepository () {
        return ingredientRepository;
    }

}
