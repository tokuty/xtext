/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.generator.parser.antlr.ex;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.generator.BindFactory;
import org.eclipse.xtext.generator.Binding;
import org.eclipse.xtext.generator.DefaultGeneratorFragment;
import org.eclipse.xtext.generator.Generator;
import org.eclipse.xtext.generator.Naming;
import org.eclipse.xtext.generator.NamingAware;
import org.eclipse.xtext.generator.NewlineNormalizer;
import org.eclipse.xtext.generator.parser.antlr.AntlrToolFacade;
import org.eclipse.xtext.generator.parser.antlr.postProcessing.SuppressWarningsProcessor;
import org.eclipse.xtext.parser.antlr.Lexer;
import org.eclipse.xtext.util.Strings;

import com.google.common.collect.Lists;
import com.google.common.io.Files;


/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class ExternalAntlrLexerFragment extends DefaultGeneratorFragment implements NamingAware {

	private String lexerGrammar;

	private boolean highlighting;

	private boolean runtime;

	private boolean contentAssist;

	private List<String> antlrParams = Lists.newArrayList();
	
	private Naming naming;

	/**
	 * @since 2.7
	 */
	@Override
	public void registerNaming(Naming naming) {
		this.naming = naming;
	}
	
	private String getLineDelimiter() {
		return naming.getLineDelimiter();
	}
	
	public void addAntlrParam(String param) {
		antlrParams.add(param);
	}

	public String[] getAntlrParams() {
		ArrayList<String> params = new ArrayList<String>(antlrParams);
		// setting the default conversion timeout to 100secs.
		// There seem to be no practical situations where the NFA conversion would hang,
		// so Terence suggested here [1] to remove the option all together
		// [1] - http://antlr.1301665.n2.nabble.com/Xconversiontimeout-td5294411.html
		if (!params.contains("-Xconversiontimeout")) {
			params.add("-Xconversiontimeout");
			params.add("100000");
		}
		String[] result = params.toArray(new String[params.size()]);
		return result;
	}

	private AntlrToolFacade antlrTool = new AntlrToolFacade();

	public void setAntlrTool(AntlrToolFacade facade) {
		this.antlrTool = facade;
	}

	public AntlrToolFacade getAntlrTool() {
		return antlrTool;
	}

	@Override
	public void generate(Grammar grammar, XpandExecutionContext ctx) {
		super.generate(grammar, ctx);
		String srcGen = Generator.SRC_GEN;
		String src = Generator.SRC;
		if (contentAssist || highlighting) {
			srcGen = Generator.SRC_GEN_IDE;
			src = Generator.SRC_IDE;
		}
		String srcGenPath = ctx.getOutput().getOutlet(srcGen).getPath();
		String srcPath = ctx.getOutput().getOutlet(src).getPath();
		String grammarFile = srcPath + "/" + getLexerGrammar().replace('.', '/') + ".g";
		String generateTo = "";
		if (lexerGrammar.lastIndexOf('.') != -1) {
			generateTo = lexerGrammar.substring(0, lexerGrammar.lastIndexOf('.'));
		}
		generateTo = srcGenPath + "/" + generateTo.replace('.', '/');
		addAntlrParam("-fo");
		addAntlrParam(generateTo);
		final String encoding = getEncoding(ctx, srcGen);
		getAntlrTool().runWithEncodingAndParams(grammarFile, encoding, getAntlrParams());

		String javaFile = srcGenPath+"/"+getLexerGrammar().replace('.', '/')+".java";
		suppressWarningsImpl(javaFile, Charset.forName(encoding));
		normalizeTokens(javaFile, Charset.forName(encoding));
	}
	
	private void normalizeTokens(String grammarFileName, Charset encoding) {
		String tokenFile = toTokenFileName(grammarFileName);
		String content = readFileIntoString(tokenFile, encoding);
		content = new NewlineNormalizer(getLineDelimiter()).normalizeLineDelimiters(content);
		List<String> splitted = Strings.split(content, getLineDelimiter());
		Collections.sort(splitted);
		content = Strings.concat(getLineDelimiter(), splitted) + getLineDelimiter();
		writeStringIntoFile(tokenFile, content, encoding);
	}
	
	private String toTokenFileName(String grammarFileName) {
		return grammarFileName.replaceAll("\\.java$", ".tokens");
	}
	
	private String getEncoding(XpandExecutionContext xpt, String outlet) {
		return xpt.getOutput().getOutlet(outlet).getFileEncoding();
	}
	
	/**
	 * @deprecated use {@link #suppressWarningsImpl(String, Charset)} instead
	 */
	@Deprecated
	protected void suppressWarningsImpl(String javaFile) {
		suppressWarningsImpl(javaFile, Charset.defaultCharset());
	}

	/**
	 * @since 2.7
	 */
	protected void suppressWarningsImpl(String javaFile, Charset encoding) {
		String content = readFileIntoString(javaFile, encoding);
		content = new SuppressWarningsProcessor().process(content);
		writeStringIntoFile(javaFile, content, encoding);
	}

	@Override
	public void checkConfiguration(Issues issues) {
		if (contentAssist && highlighting || runtime && highlighting || contentAssist && runtime) {
			issues.addError("Only one of those flags is allowed: contentAssist, runtime, highlighting flag");
		}
	}

	@Override
	public Set<Binding> getGuiceBindingsRt(Grammar grammar) {
		if (runtime)
			return new BindFactory()
				.addConfiguredBinding("RuntimeLexer",
						"binder.bind(" + Lexer.class.getName() + ".class)"+
						".annotatedWith(com.google.inject.name.Names.named(" +
						"org.eclipse.xtext.parser.antlr.LexerBindings.RUNTIME" +
						")).to(" + lexerGrammar +".class)")
				.getBindings();
		return Collections.emptySet();
	}

	@Override
	public Set<Binding> getGuiceBindingsUi(Grammar grammar) {
		if (highlighting)
			return new BindFactory()
				.addConfiguredBinding("HighlightingLexer",
						"binder.bind(" + Lexer.class.getName() + ".class)"+
						".annotatedWith(com.google.inject.name.Names.named(" +
						"org.eclipse.xtext.ide.LexerIdeBindings.HIGHLIGHTING" +
						")).to(" + lexerGrammar +".class)")
				.getBindings();
		if (contentAssist)
			return new BindFactory()
				.addConfiguredBinding("ContentAssistLexer",
						"binder.bind(org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer.class)"+
						".annotatedWith(com.google.inject.name.Names.named(" +
						"org.eclipse.xtext.ide.LexerIdeBindings.CONTENT_ASSIST" +
						")).to(" + lexerGrammar +".class)")
				.getBindings();
		return Collections.emptySet();

	}

	public void setLexerGrammar(String lexerGrammar) {
		this.lexerGrammar = lexerGrammar;
	}

	public String getLexerGrammar() {
		return lexerGrammar;
	}

	public void setHighlighting(boolean highlighting) {
		this.highlighting = highlighting;
	}

	public boolean isHighlighting() {
		return highlighting;
	}

	public void setRuntime(boolean runtime) {
		this.runtime = runtime;
	}

	public boolean isRuntime() {
		return runtime;
	}

	public void setContentAssist(boolean contentAssist) {
		this.contentAssist = contentAssist;
	}

	public boolean isContentAssist() {
		return contentAssist;
	}
	
	private String readFileIntoString(String filename, Charset encoding) {
		try {
			String result = Files.toString(new File(filename), encoding);
			return result;
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}

	private void writeStringIntoFile(String filename, String content, Charset encoding) {
		try {
			Files.write(content, new File(filename), encoding);
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}

}
