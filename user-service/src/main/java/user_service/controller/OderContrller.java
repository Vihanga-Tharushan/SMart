package user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OderContrller {

    @GetMapping("/order")
    public String getOrder(@RequestParam String item) {
        
        return "You have ordered: " + item;
    }

}
