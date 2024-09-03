package ar.edu.utn.frbb.tup.controladores;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ControladorClientes {
    private ServicioClientes servicioClientes;
    private ValidacionesServicios validacionesServicios;

    public ControladorClientes (ServicioClientes servicioClientes, ValidacionesServicios validacionesServicios) {
        this.servicioClientes = servicioClientes;
        this.validacionesServicios = validacionesServicios;
        servicioClientes.inicializarClientes();
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() throws ClientesVaciosException {
        List<Cliente> clientes = servicioClientes.findAllClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteExistenteException, ClientesVaciosException {
        validacionesServicios.validarDni(clienteDto.getDni());
        validacionesServicios.validarTipoPersona(clienteDto.getTipoPersona());
        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco
        Cliente cliente = servicioClientes.crearCliente(bancoProvincia.getClientes());
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> borrarCliente(@PathVariable long dni) throws ClienteNoEncontradoException, ClientesVaciosException {
        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco
        servicioClientes.elimimarCliente(bancoProvincia.getClientes());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping
    public ResponseEntity<Cliente> modificarCliente(@RequestBody ClienteDto clienteDto) throws ClienteNoEncontradoException, ClientesVaciosException {
        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco
        Cliente cliente = servicioClientes.modificarCliente(bancoProvincia.getClientes());
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }
}
