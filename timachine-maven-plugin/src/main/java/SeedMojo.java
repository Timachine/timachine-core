import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Populate seed data.
 */
@Mojo(name = "seed")
@Execute(phase = LifecyclePhase.COMPILE)
public class SeedMojo extends AbstractMojo {

    @Parameter(required = true)
    private String packageName;
    @Parameter(property = "project.runtimeClasspathElements")
    private List<String> elements;

    @Parameter(property = "project.build.outputDirectory")
    private String outDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Thread.currentThread().setContextClassLoader(getClassLoader());
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(packageName + ".Seeds");
            Method method = clazz.getMethod("main", String[].class);
            String[] args = null;
            method.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new MojoExecutionException("Failed to execute seed", e);
        }
    }

    private ClassLoader getClassLoader() throws MojoExecutionException {
        elements.add(outDir);
        try {
            URL urls[] = new URL[elements.size()];

            for (int i = 0; i < elements.size(); ++i) {
                urls[i] = new File((String) elements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e)//gotta catch em all
        {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

}
