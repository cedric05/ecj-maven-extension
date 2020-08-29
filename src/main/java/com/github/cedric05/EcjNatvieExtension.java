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
		String profile = System.getProperty("ecj-native", "");
		try {
			Xpp3Dom aConfig = getConfig(profile);
			if (aConfig != null) {
				for (MavenProject currentProject : session.getAllProjects()) {
					Plugin plugin = currentProject.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
					if (plugin != null && plugin.getExecutions() != null)
						for (PluginExecution execution : plugin.getExecutions()) {
							if (execution.getId().equals("default-compile")) {
								Xpp3Dom dependencyConfig = aConfig.getChild("dependencies");
								List<Dependency> dependencies = getDependencies(dependencyConfig);
								execution.setConfiguration(aConfig.getChild("configuration"));
								currentProject.getDependencies().addAll(dependencies);
							}
						}
				}
			}
		} catch (Exception e) {
			throw new MavenExecutionException("compiler plugin modification failed, could be compile-config.xml not available", e);
		}
	}

	private static List<Dependency> getDependencies(Xpp3Dom dom) {
		ArrayList<Dependency> list = new ArrayList<Dependency>();
		Xpp3Dom[] children = dom.getChildren();
		for (Xpp3Dom child : children) {
			Xpp3Dom version = child.getChild("version");
			Xpp3Dom artifactId = child.getChild("artifactId");
			Xpp3Dom groupId = child.getChild("groupId");
			Xpp3Dom scope = child.getChild("scope");
			Xpp3Dom systemPath = child.getChild("systemPath");
			Dependency dependency = new Dependency();
			dependency.setGroupId(groupId.getValue());
			dependency.setArtifactId(version.getValue());
			dependency.setVersion(artifactId.getValue());
			dependency.setScope(scope.getValue());
			dependency.setSystemPath(systemPath.getValue());
			list.add(dependency);
		}
		return list;
	}

	public static Xpp3Dom getConfig(String name) throws XmlPullParserException, IOException {
		String homedir = System.getProperty("user.home");
		FileInputStream inputStream = new FileInputStream(homedir + "/.m2/compile-config.xml");
		Xpp3Dom config = Xpp3DomBuilder.build(inputStream, "utf-8");
		for (Xpp3Dom child : config.getChildren()) {
			String attribute = child.getAttribute("id");
			if (attribute != null && attribute.equals(name)) {
				return child;
			}
		}
		return null;
	}
}