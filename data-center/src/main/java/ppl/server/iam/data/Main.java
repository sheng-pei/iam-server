package ppl.server.iam.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ppl.server.iam.authn.UserService;
import ppl.server.iam.data.controller.authn.UserController;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
        ApplicationContext context = SpringApplication.run(Main.class);
//        Class<UserService> clazz = UserService.class;
//        Method method = clazz.getDeclaredMethods()[0];
//        String interfaceName = method.getName();
//        Class<?> returnType = method.getReturnType();
//        Parameter[] parameters = method.getParameters();
    }
}
