package com.stonemason.deadletterexchange;

import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.Random;

import static com.stonemason.deadletterexchange.Consts.*;

@Data
@ToString
public class Student {

    public int id;
    public String name;
    public String surname;
    public String nameSurname;
    public Boolean availableToApplication;
    public String applicationDate;

    public Student() {
        this.id = (int) (Math.random()*(maxId - minId + 1) + minId);
        this.name = names[new Random().nextInt(5)];
        this.surname = surnames[new Random().nextInt(5)];
        this.nameSurname = this.name + " " + this.surname;
        // set true if you dont want to see else condition that messsage has not send
        this.availableToApplication = new Random().nextBoolean();
        this.applicationDate = LocalDateTime.now().toString();
    }
}
