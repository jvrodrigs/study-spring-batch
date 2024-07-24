package br.com.joao.backend.transaction;

import br.com.joao.backend.shared.api.MessageRequest;
import br.com.joao.backend.transaction.api.TransactionFilterApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("cnab")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
       transactionService.uploadCnabFile(file);
       return ResponseEntity.ok(new MessageRequest("Processamento Iniciado..", HttpStatus.OK.value()));
    }

    @GetMapping("all")
    public ResponseEntity<?> all(
            TransactionFilterApi filter,
            @RequestParam(name= "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(transactionService.findAll(filter, page, size));
    }
}
