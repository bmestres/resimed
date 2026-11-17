package cat.itacademy.s05.appointmentsapp;

import org.springframework.boot.SpringApplication;

public class TestAppointmentsAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(AppointmentsAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
