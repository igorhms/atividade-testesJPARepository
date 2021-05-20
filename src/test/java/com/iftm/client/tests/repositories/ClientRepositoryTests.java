package com.iftm.client.tests.repositories;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
	
	@Autowired
	private ClientRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private String nonExistingName;
	private String nonExistingCpf;
	private long countTotalClients;
	private long countClientByIncome;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		nonExistingName = "Igor Silva";
		nonExistingCpf = "123123123";
		countTotalClients = 12L;
		countClientByIncome = 5L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		repository.deleteById(existingId);
		
		Optional<Client> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
		
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Client client = ClientFactory.createClient();
		client.setId(null);
		
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		
		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients + 1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client);		
	}
		
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		
		Double income = 4000.0;
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<Client> result = repository.findByIncome(income, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	
	// Find para nome existente
	@Test
	public void findByNameShouldReturnObjectsWhenClientHaveTheSeachedName() {
		
		String name = "Jose";
		PageRequest pageRequest = PageRequest.of(0, 10);
			
		Page<Client> result = repository.findByName(name, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(1, result.getTotalElements());
	}
	
	// Find para nome existente ignorando case
	@Test
	public void findByNameShouldReturnObjectsWhenClientHaveTheSeachedNameIgnoringCase() {
		
		String name = "jose";
		PageRequest pageRequest = PageRequest.of(0, 10);
				
		Page<Client> result = repository.findByName(name, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(1, result.getTotalElements());
	}
	
	// Find para nome vazio retornando todos clientes	
	@Test
	public void findByEmptyNameShouldReturnAllClients() {
		
		String name = "";
		PageRequest pageRequest = PageRequest.of(0, 10);
			
		Page<Client> result = repository.findByName(name, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalClients, result.getTotalElements());
	}
	
	// Find para data de nascimento maior que determinado data de referência
	@Test
	public void findByYearShouldReturnClientsWhenYearOfBirthDateIsEqualThanSearched() {
		
		Instant referenceDate =  Instant.parse("2020-07-12T20:50:00Z");
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		
		Page<Client> result = repository.findByYear(referenceDate, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(1, result.getTotalElements());
	}

	// Teste no update de dados de clientes
	@Test
	public void updateShouldUpdateACLientWhenIdExists() {
	
		Client client = ClientFactory.createClient();
		client.setName(nonExistingName);
		client.setCpf(nonExistingCpf);
		
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		
		Assertions.assertEquals(result.get().getId(), 1);
		Assertions.assertEquals(result.get().getName(), nonExistingName);
		Assertions.assertEquals(result.get().getCpf(), nonExistingCpf);
	}
}
