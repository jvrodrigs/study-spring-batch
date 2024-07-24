package br.com.joao.backend.transaction;

import br.com.joao.backend.transaction.api.TransactionFilterApi;
import jakarta.persistence.criteria.Predicate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final Path fileStorageLocation;
    private final JobLauncher jobLauncher;
    private final Job job;

    public TransactionService(
            TransactionRepository repository,
            @Value("${file.upload-dir}") String fileUploadDir,
            @Qualifier("jobLauncherAsync") JobLauncher jobLauncher,
            Job job){
        this.repository = repository;
        this.fileStorageLocation = Paths.get(fileUploadDir);
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public List<Transaction> findAll(TransactionFilterApi filter, int page, int size) {
        return repository.findAll(getFilter(filter), PageRequest.of(page, size)).getContent();
    }

    private Specification<Transaction> getFilter(TransactionFilterApi filterApi) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterApi.getOwner() != null){
                predicates.add(builder.equal(root.get("owner"), filterApi.getOwner()));
            }

            if (filterApi.getName() != null){
                predicates.add(builder.equal(root.get("name"), filterApi.getName()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public void uploadCnabFile(MultipartFile file) throws Exception {
        var fileName = StringUtils.cleanPath(file.getOriginalFilename());
        var targetLocation = fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);

        var jobParams = new JobParametersBuilder()
                .addJobParameter(
                        "cnab",
                        file.getOriginalFilename(),
                        String.class,
                        true
                )
                .addJobParameter(
                        "cnabFile",
                        "file:" + targetLocation.toString(),
                        String.class,
                        false)
                .toJobParameters();

        jobLauncher.run(job, jobParams);
    }
}
