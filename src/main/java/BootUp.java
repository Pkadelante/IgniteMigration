import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
@EnableAutoConfiguration
public class BootUp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BootUp.class, args);
    }


    @Bean
    private IgniteConfiguration igniteConfiguration() {
        return new IgniteConfiguration()
                .setCacheConfiguration(userCacheConfiguration())
                .setClientMode(false)
                ;

    }

    private CacheConfiguration userCacheConfiguration() {
        return new CacheConfiguration()
                .setName("UserCache");
    }


    @Bean
    public Ignite ignite(IgniteConfiguration configuration) {
        Ignite ignite = Ignition.start(configuration);
        if (!ignite.active()) ignite.active(true);

        return ignite;
    }

}
