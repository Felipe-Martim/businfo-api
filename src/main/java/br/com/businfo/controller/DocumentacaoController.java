package br.com.businfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Hidden;

/** Atalho /openapi.json para a especificação gerada em /api/v1/openapi.json. */
@Hidden
@Controller
public class DocumentacaoController {

    @GetMapping("/openapi.json")
    public String openApi() {
        return "forward:/api/v1/openapi.json";
    }
}
