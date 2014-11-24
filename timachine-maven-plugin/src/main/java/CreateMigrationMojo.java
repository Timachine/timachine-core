import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import jp.co.worksap.timachine.MigrationNameResolver;
import jp.co.worksap.timachine.model.MigrationType;
import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Create a migration.
 * <p>Parameters:  use -D to specify</p>
 * <p>name : name appended to the generated class name</p>
 * <p>type : type of this migration, (MAIN, TEST) as options</p>
 */
@Mojo(name = "create-migration")
public class CreateMigrationMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)
    private String sourceDir;

    @Parameter(required = true, readonly = true)
    @Getter
    private String packageName;

    @Parameter(defaultValue = "${project.build.testSourceDirectory}", readonly = true)
    private String testDir;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File baseDir;

    @Parameter(required = true)
    @Getter
    private String testPackageName;

    @Parameter(defaultValue = "migration")
    private String templateName;

    @Getter
    private String className = "";

    @Parameter(property = "name", defaultValue = "")
    private String name;

    @Parameter(property = "type", defaultValue = "MAIN")
    private String type;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setClassForTemplateLoading(CreateMigrationMojo.class, "templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        try {
            Template template = cfg.getTemplate(templateName + ".ftl");
            MigrationType migrationType = MigrationType.valueOf(type);
            String dir;
            String pkg;
            switch (migrationType) {
                case MAIN:
                    dir = sourceDir;
                    pkg = packageName;
                    break;
                case TEST:
                    dir = testDir;
                    pkg = testPackageName;
                    break;
                default:
                    throw new MojoExecutionException("Unexpected type");
            }
            this.className = MigrationNameResolver.generateClassName(name, MigrationType.valueOf(type));
            String[] splitted = pkg.split("[.]");
            Path path = Paths.get(dir, splitted).resolve(this.className + ".java");
            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            template.process(this, writer);
            getLog().info("Migration file generated in " + Paths.get(baseDir.toURI()).relativize(path));
        } catch (IOException | TemplateException e) {
            throw new MojoExecutionException("Failed to process template", e);
        }
    }
}
