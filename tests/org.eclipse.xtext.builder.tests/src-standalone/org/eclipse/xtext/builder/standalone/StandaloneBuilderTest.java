/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.builder.standalone;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.generator.OutputConfiguration.SourceMapping;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.util.Files;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * @author Stefan Oehme - Initial contribution and API
 */
@RunWith(XtextRunner.class)
@InjectWith(StandaloneBuilderInjectorProvider.class)
public class StandaloneBuilderTest {

	private static final File PROJECT_DIR = new File("test-data/standalone");
	private static final File TMP_DIR = new File(PROJECT_DIR, "tmp");
	@Inject
	private StandaloneBuilder builder;

	@After
	public void cleanup() throws IOException {
		deleteFolder("src-gen");
		deleteFolder("src2-gen");
		if (TMP_DIR.exists()) {
			Files.sweepFolder(TMP_DIR);
			TMP_DIR.delete();
		}
	}

	@Test
	public void testDifferentOutputFolders() {
		initBuilder(new TestLanguageConfiguration(true));
		assertTrue(builder.launch());

		File generatedFile = getFile("src-gen/Foo.txt");
		assertTrue(generatedFile.exists());
		generatedFile = getFile("src2-gen/Bar.txt");
		assertTrue(generatedFile.exists());

		File unexpectedFile = getFile("src-gen/Bar.txt");
		assertFalse(unexpectedFile.exists());
		unexpectedFile = getFile("src2-gen/Foo.txt");
		assertFalse(unexpectedFile.exists());
	}

	@Test
	public void testSameOutputFolder() {
		initBuilder(new TestLanguageConfiguration(false));
		assertTrue(builder.launch());

		File generatedFile = getFile("src-gen/Foo.txt");
		assertTrue(generatedFile.exists());
		generatedFile = getFile("src-gen/Bar.txt");
		assertTrue(generatedFile.exists());

		File unexpectedFile = getFile("src2-gen/Bar.txt");
		assertFalse(unexpectedFile.exists());
		unexpectedFile = getFile("src2-gen/Foo.txt");
		assertFalse(unexpectedFile.exists());
	}

	@Test
	public void testOnlyOneSourceFolder() {
		initBuilder(new TestLanguageConfiguration(false));
		builder.setSourceDirs(ImmutableList.of(new File(PROJECT_DIR, "src").getAbsolutePath()));
		assertTrue(builder.launch());

		File generatedFile = getFile("src-gen/Foo.txt");
		assertTrue(generatedFile.exists());

		File unexpectedFile = getFile("src-gen/Bar.txt");
		assertFalse(unexpectedFile.exists());
		unexpectedFile = getFile("src2-gen/Bar.txt");
		assertFalse(unexpectedFile.exists());
		unexpectedFile = getFile("src2-gen/Foo.txt");
		assertFalse(unexpectedFile.exists());
	}

	@Test
	public void testRelativeSourceFolder() {
		initBuilder(new TestLanguageConfiguration(false));
		builder.setSourceDirs(ImmutableList.of("test-data/standalone/src"));
		assertTrue(builder.launch());

		File generatedFile = getFile("src-gen/Foo.txt");
		assertTrue(generatedFile.exists());

		File unexpectedFile = getFile("src-gen/Bar.txt");
		assertFalse(unexpectedFile.exists());
		unexpectedFile = getFile("src2-gen/Bar.txt");
		assertFalse(unexpectedFile.exists());
		unexpectedFile = getFile("src2-gen/Foo.txt");
		assertFalse(unexpectedFile.exists());
	}

	@Test
	public void testJarToPlatformMapping() {
		initBuilder(new TestLanguageConfiguration(false));
		builder.setSourceDirs(ImmutableList.of("test-data/standalone.with.reference/model"));
		builder.setClassPathEntries(ImmutableList.of("test-data/standalone.with.reference/target/classes/",
				"test-data/model.in.eclipse.project.jar"));

		assertTrue("Builder launch returned false", builder.launch());
		URI uri = EcorePlugin.getPlatformResourceMap().get("model.in.eclipse.project");
		assertNotNull("No platform mapping found for 'model.in.eclipse.project'", uri);
		assertTrue("Platform mapping is archive", uri.toString().startsWith("archive:file:/"));
		assertTrue("Platform mapping points to jared project",
				uri.toString().endsWith("test-data/model.in.eclipse.project.jar!/"));
	}

	@Test
	public void testDuplicateSourceEntries() {
		TestLanguageConfiguration config = new TestLanguageConfiguration(false);
		config.setJavaSupport(true);
		initBuilder(config);
		builder.setJavaSourceDirs(ImmutableList.of(new File(PROJECT_DIR, "src2").getPath()));
		builder.setTempDir(TMP_DIR);
		builder.setDebugLog(true);
		assertTrue("Builder launch returned false", builder.launch());
		File compiledClazz = getFile("tmp/classes/JavaClass.class");
		assertTrue("java compilation failed", compiledClazz.exists());

	}

	private File getFile(String projectRelativePath) {
		return new File(PROJECT_DIR, projectRelativePath);
	}

	private void deleteFolder(String projectRelativePath) throws FileNotFoundException {
		File folder = getFile(projectRelativePath);
		if (folder.exists()) {
			Files.sweepFolder(folder);
			folder.delete();
		}
	}

	private StandaloneBuilder initBuilder(ILanguageConfiguration config) {
		String sourceDir1 = new File(PROJECT_DIR, "src").getAbsolutePath();
		String sourceDir2 = new File(PROJECT_DIR, "src2").getAbsolutePath();
		builder.setSourceDirs(ImmutableList.of(sourceDir1, sourceDir2));
		Map<String, LanguageAccess> languages = new LanguageAccessFactory().createLanguageAccess(
				ImmutableList.of(config), getClass().getClassLoader(), PROJECT_DIR);
		builder.setLanguages(languages);
		builder.setClassPathEntries(ImmutableList.<String> of());
		return builder;
	}

	public static class TestLanguageConfiguration implements ILanguageConfiguration {

		private boolean useOutputPerSource;
		private boolean javaSupport = false;

		public TestLanguageConfiguration(boolean useOutputPerSource) {
			this.useOutputPerSource = useOutputPerSource;
		}

		/* @NonNull */
		@Override
		public String getSetup() {
			return "org.eclipse.xtext.builder.tests.BuilderTestLanguageStandaloneSetup";
		}

		@Override
		public Set<OutputConfiguration> getOutputConfigurations() {
			OutputConfiguration config = new OutputConfiguration(IFileSystemAccess.DEFAULT_OUTPUT);
			config.setOutputDirectory("src-gen");
			if (useOutputPerSource) {
				SourceMapping sourceMapping = new OutputConfiguration.SourceMapping("src2");
				sourceMapping.setOutputDirectory("src2-gen");
				config.getSourceMappings().add(sourceMapping);
				config.setUseOutputPerSourceFolder(true);
			}
			return ImmutableSet.of(config);
		}

		public void setJavaSupport(boolean javaSupport) {
			this.javaSupport = javaSupport;
		}

		@Override
		public boolean isJavaSupport() {
			return javaSupport;
		}
	}
}
