import jp.co.worksap.timachine.model.Migration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

/**
 * Created by liuyang on 14-11-7.
 */
@Mojo(name = "migrate",requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MigrateMojo extends AbstractMojo {


    @Parameter(required = true)
    private String packageName;
    @Parameter(property = "project.runtimeClasspathElements")
    private List<String> elements;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" classpath" + elements.toString());
        URL[] runtimeUrls = new URL[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            String element = (String) elements.get(i);
            try {
                runtimeUrls[i] = new File(element).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Failed to load project classpath", e);
            }
        }
        URLClassLoader newLoader = new URLClassLoader(runtimeUrls,
                Thread.currentThread().getContextClassLoader());
        Reflections reflections = new Reflections(new ConfigurationBuilder().addClassLoader(newLoader).forPackages(packageName));
        Set<Class<?>> migrations = reflections.getTypesAnnotatedWith(Migration.class);
        getLog().info(migrations.toString());
    }
}
