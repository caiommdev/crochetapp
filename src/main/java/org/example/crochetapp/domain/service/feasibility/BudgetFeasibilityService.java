package org.example.crochetapp.domain.service.feasibility;

import java.util.List;

import org.example.crochetapp.domain.model.Product;
import org.example.crochetapp.domain.model.material.Material;
import org.springframework.stereotype.Service;

@Service
public class BudgetFeasibilityService {
    
    public FeasibilityResult checkFeasibility(Product product, List<Material> materials){
        for (Material reqMaterial : product.getRequiredMaterials()) {
            if (!materials.contains(reqMaterial)) {
                return FeasibilityResult.isNotFeasible("Material not available: " + reqMaterial.getName());
            }
        }
        return FeasibilityResult.isFeasible();
    }
}
