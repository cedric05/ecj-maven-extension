package com.github.cedric05;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * ` scans of all the source code if the compiler has to have the entire set of
 * sources. This is currently the case for at least the C# compiler and most
 * likely all the other .NET compilers too.
 *
 * @author <a href="mailto:kesavarapu.siva@gmail.com">Prasanth</a>
 */
@Component(role = AbstractMavenLifecycleParticipant.class)
public class EcjNatvieExtension extends AbstractMavenLifecycleParticipant {

	@Override
	public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
		String flag = System.getProperty("ecj-native", "f").toLowerCase();
		if (flag.startsWith("t") || flag.startsWith("1"))
			try {
				for (MavenProject currentProject : session.getAllProjects()) {
					System.out.println(currentProject.getArtifact());
					Plugin plugin = currentProject.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
					if (plugin != null && plugin.getExecutions() != null)
						for (PluginExecution execution : plugin.getExecutions()) {
							if (execution.getId().equals("default-compile")) {
								execution.setConfiguration(getConfig());
								List<Dependency> dependencies = currentProject.getDependencies();
								dependencies.addAll(getJava8Deps());
							}
						}
				}
			} catch (Exception ex) {
				throw new MavenExecutionException("compiler plugin modification failed, could be compile-config.xml not available", ex);
			}
	}

	private static List<Dependency> getJava8Deps() {
		List<Dependency> dep = new ArrayList<Dependency>();
		dep.add(getADepFromFilename("rt.jar"));
		dep.add(getADepFromFilename("jce.jar"));
		dep.add(getADepFromFilename("jsse.jar"));
		dep.add(getADepFromFilename("management-agent.jar"));
		dep.add(getADepFromFilename("resources.jar"));
		return dep;
	}

	private static Dependency getADepFromFilename(String filename) {
		String home = System.getProperty("java.home");
		Dependency dep = new Dependency();
		dep.setGroupId(filename);
		dep.setArtifactId(filename);
		dep.setVersion(filename);
		dep.setScope("system");
		dep.setSystemPath(home + "/lib/" + filename);
		return dep;
	}

	public static Xpp3Dom getConfig() throws XmlPullParserException, IOException {
		String homedir = System.getProperty("user.home");
		FileInputStream inputStream = new FileInputStream(homedir + "/.m2/compile-config.xml");
		Xpp3Dom build = Xpp3DomBuilder.build(inputStream, "utf-8");
		return build;
	}
}