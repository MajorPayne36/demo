package com.bank.demo.controllers;

import com.bank.demo.exceptions.ResourceNotFoundException;
import com.bank.demo.utils.JDBCUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins="http://localhost:3000")
@RequestMapping("/bank")
public class DML {

    static final String NUMBER = "number";
    static final String PIN = "pin";
    static final String MONEY = "money";
    /**
     * DML operations util
     */
    private JDBCUtil util = new JDBCUtil();

    /**
     * Delete previous ACCOUNTT table
     * @return true if it successful
     */
    @GetMapping(value = "/drop")
    public boolean dropTable() {
        return util.dropTable();
    }

    @PostMapping(value = "/authorize", consumes = "application/json", produces = "application/json")
    public JsonNode authorize(@RequestBody JsonNode jsonNode) throws ResourceNotFoundException {

        JsonNode number = jsonNode.path(NUMBER);
        JsonNode pin = jsonNode.path(PIN);

        System.out.println(number.asText() + " " + pin.asText());

        checkJsonNode(number, pin);
        return util.getAccount(number.asText(), pin.asText());
    }

    @PostMapping(value = "/operation", consumes = "application/json", produces = "application/json")
    public boolean giveMoney (@RequestBody JsonNode jsonNode) throws ResourceNotFoundException {
        JsonNode number = jsonNode.path(NUMBER);
        JsonNode pin = jsonNode.path(PIN);
        JsonNode money = jsonNode.path(MONEY);

        checkJsonNode(number, pin, money);

        return util.giveMoney(number.asText(), pin.asText(), money.asInt());
    }

    public static void checkJsonNode(JsonNode... jsonNodes) throws ResourceNotFoundException {
        for (JsonNode node : jsonNodes) {
            if (node.isMissingNode()) {
                throw new ResourceNotFoundException("Mandatory field is missing");
            }
        }
    }

}
