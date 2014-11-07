import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import jp.co.worksap.timachine.MigrationNameResolver;
import jp.co.worksap.timachine.MigrationType;
import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
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

    @Parameter(property = "project.build.sourceDirectory")
    private String sourceDir;

    @Parameter(required = true)
    @Getter
    private String packageName;

    @Parameter(defaultValue = "migration")
    private String templateName;

    @Getter
    private String className = "";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setClassForTemplateLoading(CreateMigrationMojo.class, "templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        try {
            Template template = cfg.getTemplate(templateName + ".ftl");
            String name = System.getProperty("name");
            String type = System.getProperty("type");
            if (type == null) {
                type = "MAIN";
            }
            this.className = MigrationNameResolver.generateClassName(name, MigrationType.valueOf(type));
            String[] splitted = packageName.split("[.]");
            Path path = Paths.get(sourceDir, splitted).resolve(this.className + ".java");
            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            template.process(this, writer);
            getLog().info("Migration file generated in " + Paths.get(sourceDir).relativize(path));
        } catch (IOException | TemplateException e) {
            throw new MojoExecutionException("Failed to process template", e);
        }
    }
}
