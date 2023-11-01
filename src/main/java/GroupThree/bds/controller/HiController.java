package GroupThree.bds.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/hi")
public class HiController {
    @GetMapping
    public ResponseEntity<?> sayHi(){
        return ResponseEntity.ok("Say Hi");
    }
}
