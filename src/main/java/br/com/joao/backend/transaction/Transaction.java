package br.com.joao.backend.transaction;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    private Long id;
    private Integer type;
    private Date date;
    private BigDecimal value;
    private Long cpf;
    private String card;
    private Time hour;
    private String owner;
    private String name;

    public Transaction(
            Long id, Integer type, Date date,
            BigDecimal value, Long cpf, String card,
            Time hour, String owner, String name) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.value = value;
        this.cpf = cpf;
        this.card = card;
        this.hour = hour;
        this.owner = owner;
        this.name = name;
    }

    public Transaction() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public Time getHour() {
        return hour;
    }

    public void setHour(Time hour) {
        this.hour = hour;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal withValue(BigDecimal valueCurrent) {
        return valueCurrent.divide(BigDecimal.valueOf(100));
    }

    public Date withDate(String value) throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyyMMdd");
        var date = dateFormat.parse(value);

        return new Date(date.getTime());
    }

    public Time withHour(String hour) throws ParseException {
        var dateFormat = new SimpleDateFormat("HHmmss");
        var date = dateFormat.parse(hour);

        return new Time(date.getTime());
    }
}
