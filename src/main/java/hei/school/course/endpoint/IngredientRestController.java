package hei.school.course.endpoint;

import edu.hei.school.restaurant.service.exception.ClientException;
import edu.hei.school.restaurant.service.exception.NotFoundException;
import edu.hei.school.restaurant.service.exception.ServerException;

import hei.school.course.endpoint.mapper.IngredientRestMapper;
import hei.school.course.endpoint.mapper.StockMovementRestMapper;
import hei.school.course.endpoint.rest.CreateIngredientPrice;
import hei.school.course.endpoint.rest.CreateOrUpdateIngredient;
import hei.school.course.endpoint.rest.IngredientRest;
import hei.school.course.endpoint.rest.StockMovementRest;
import hei.school.course.model.Ingredient;
import hei.school.course.model.Price;
import hei.school.course.model.StockMovement;
import hei.school.course.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class IngredientRestController {
    private final IngredientService ingredientService;
    private final IngredientRestMapper ingredientRestMapper;
    private final StockMovementRestMapper stockMovementRestMapper;

    @GetMapping("/ingredients")
    public ResponseEntity<Object> getIngredients(@RequestParam(name = "priceMinFilter", required = false) Double priceMinFilter,
                                                 @RequestParam(name = "priceMaxFilter", required = false) Double priceMaxFilter,
                                                 @RequestParam(name = "page", required = false) Integer page,
                                                 @RequestParam(name = "size", required = false) Integer size) {
        size = size == null ? 500 : size;
        page = page == null ? 1 : page;
        try {
            List<Ingredient> ingredientsByPrices = ingredientService.getIngredientsByPrices(priceMinFilter, priceMaxFilter, page, size);
            List<IngredientRest> ingredientRests = ingredientsByPrices.stream()
                    .map(ingredientRestMapper::toRest)
                    .toList();
            return ResponseEntity.ok().body(ingredientRests);
        } catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/ingredients")
    public ResponseEntity<Object> addIngredients(@RequestBody List<CreateOrUpdateIngredient> ingredientsToCreateOrUpdate) {
        try{
            List<Ingredient> ingredients = ingredientsToCreateOrUpdate.stream()
                    .map(ingredientRestMapper::toModel)
                    .toList();
            List<IngredientRest> ingredientRests = ingredientService.saveAll(ingredients).stream()
                    .map(ingredientRestMapper::toRest).toList();
            return ResponseEntity.ok().body(ingredientRests);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/ingredients")
    public ResponseEntity<Object> updateIngredients(@RequestBody List<CreateOrUpdateIngredient> ingredientsToCreateOrUpdate) {
        try {
            List<Ingredient> ingredients = ingredientsToCreateOrUpdate.stream()
                    .map(ingredientRestMapper::toModel)
                    .toList();
            List<IngredientRest> ingredientsRest = ingredientService.saveAll(ingredients).stream()
                    .map(ingredientRestMapper::toRest)
                    .toList();
            return ResponseEntity.ok().body(ingredientsRest);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/ingredients/{ingredientId}/prices")
    public ResponseEntity<Object> updateIngredientPrices(@PathVariable Long ingredientId, @RequestBody List<CreateIngredientPrice> ingredientPrices) {
        try{
            List<Price> prices = ingredientPrices.stream()
                    .map(ingredientPrice ->
                            new Price(ingredientPrice.getAmount(), ingredientPrice.getDateValue()))
                    .toList();
            Ingredient ingredient = ingredientService.addPrices(ingredientId, prices);
            IngredientRest ingredientRest = ingredientRestMapper.toRest(ingredient);
            return ResponseEntity.ok().body(ingredientRest);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/ingredients/{ingredientId}/stockMovements")
    public ResponseEntity<Object> updateIngredientStockMovement(@PathVariable Long ingredientId,
                                                                List<StockMovementRest> stockMovementRests) {
        try {
            List<StockMovement> stockMovements = stockMovementRests.stream()
                    .map(stockMovementRestMapper::toModel)
                    .toList();
            Ingredient ingredient = ingredientService.addStockMovements(ingredientId, stockMovements);
            IngredientRest ingredientRest = ingredientRestMapper.toRest(ingredient);
            return ResponseEntity.ok().body(ingredientRest);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Object> getIngredient(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(ingredientRestMapper.toRest(ingredientService.getById(id)));
        } catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
