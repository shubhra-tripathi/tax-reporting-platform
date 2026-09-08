package com.demo.taxreporting.mapper;

import com.demo.taxreporting.model.ClientTaxProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTaxProfileMapperTest {
     private final ClientTaxProfileMapper mapper = new ClientTaxProfileMapper();

     @Test
     void shouldCreateDeterministicTaxProfile() {

         ClientTaxProfile result = mapper.map(49108L);

         assertEquals("C49108", result.clientId());
     }
}
