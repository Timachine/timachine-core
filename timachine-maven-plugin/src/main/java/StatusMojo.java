import jp.co.worksap.timachine.Executor;
import jp.co.worksap.timachine.Migrations;
import jp.co.worksap.timachine.VersionChecker;
import jp.co.worksap.timachine.model.Migration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.reflections.Reflections;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Show migration status.
 */
@Mojo(name = "status", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
@Execute(phase = LifecyclePhase.COMPILE)
public class StatusMojo extends AbstractMojo {
    @Parameter(required = true)
    private String packageName;
    @Parameter(property = "project.runtimeClasspathElements")
    private List<String> elements;

    @Parameter(property = "project.build.outputDirectory")
    private String outDir;

    @Parameter(required = true)
    private String executor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        Thread.currentThread().setContextClassLoader(getClassLoader());
        Reflections reflections = new Reflections(packageName);
        List<Class<?>> list = new ArrayList<>(reflections.getTypesAnnotatedWith(Migration.class));
        Migrations migrations = new Migrations(list);
        ps.println();
        ps.print(String.format("Total migrations count: %d", migrations.getVersions().size()));
        if (migrations.getVersions().isEmpty()) {
            ps.println();
        } else {
            ps.println(String.format(", from %s to %s", migrations.getVersions().get(0), migrations.getVersions().get(migrations.getVersions().size() - 1)));
        }
        try {
            Executor executorImpl = (Executor) Thread.currentThread().getContextClassLoader().loadClass(executor).newInstance();
            String currentVersion = executorImpl.getVersionProvider().currentVersion();
            String versionStr;
            if (currentVersion == null) {
                currentVersion = VersionChecker.INIT_VERSION;
            }
            versionStr = currentVersion;
            ps.print(String.format("Current version: %s", versionStr));
            if (!migrations.getVersions().isEmpty()) {
                String status;
                String latestVersion = migrations.getVersions().get(migrations.getVersions().size() - 1);
                if (VersionChecker.INIT_VERSION.equals(currentVersion)) {
                    int behind = migrations.getVersions().size();
                    status = String.format("behind %d steps", behind);
                } else if (!migrations.getVersions().contains(currentVersion)) {
                    status = "ERROR! Not tracked in migrations";
                } else if (currentVersion.equals(latestVersion)) {
                    status = "up to date";
                } else {
                    int behind = migrations.getVersions().size() - 1 - migrations.getVersions().indexOf(currentVersion);
                    status = String.format("behind %d steps", behind);
                }
                ps.println(", " + status);
            }

            ps.println();
            System.out.println(os.toString());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to initialize dependencies.", e);
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
