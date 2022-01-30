package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService{

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	// Strategy: Implementar os métodos definidos na interface.	
	// Facade: Abstrair integracoes com subsistemas, provendo uma interface simples.
	
	@Override
	public Iterable<Cliente> buscarTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		// FIXME Buscar Cliente por ID, caso exista:
		Cliente clienteBD = buscarPorId(id);
		
		if (clienteBD != null) {
			// FIXME Verificar se o Endereco do Cliente já existe (pelo CEP).		
			// FIXME Caso não exista, integrar com o ViaCEP e persitir o retorno;		
			// FIXME Alterar Cliente, vinculado ao Endereco (novo ou já existente)
			salvarClienteComCep(clienteBD);
		} 
	}

	@Override
	public void deletar(Long id) {
		// Deletar cliente por ID.
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// FIXME verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Long longCep = Long.parseLong(cep);
		Optional<Endereco> endereco = enderecoRepository.findById(longCep);
		Endereco end = endereco.get();
		// FIXME Caso não exista, integrar com o ViaCEP e persistir o retorno.
		if (end == null) {
			Endereco novoEndereco = viaCepService.consultarCep(null);
			enderecoRepository.save(novoEndereco);
			end = novoEndereco;
		}		
		cliente.setEndereco(end);		
		// FIXME Inserir Cliente, vinculado ao Endereco (novo ou já existente)
		clienteRepository.save(cliente);
	}
	
	
	
}
