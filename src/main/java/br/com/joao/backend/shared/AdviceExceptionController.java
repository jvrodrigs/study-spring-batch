package br.com.joao.backend.shared;

import br.com.joao.backend.shared.api.MessageRequest;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceExceptionController {

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    public ResponseEntity<MessageRequest> handleJobInstanceAlreadyCompleteException(JobInstanceAlreadyCompleteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new MessageRequest("O arquivo enviado já foi importado no sistema.", HttpStatus.CONFLICT.value())
                );
    }
}
