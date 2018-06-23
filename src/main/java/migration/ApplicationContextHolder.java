package migration;

import org.springframework.context.ApplicationContext;

import javax.validation.Valid;

public class ApplicationContextHolder {
    private static ApplicationContext applicationContext;

    public static void set(ApplicationContext applicationContext) {
        if (ApplicationContextHolder.applicationContext != null) {
            throw new RuntimeException("Double write applicationContext");
        }
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext get() {
        return ApplicationContextHolder.applicationContext;
    }
}
