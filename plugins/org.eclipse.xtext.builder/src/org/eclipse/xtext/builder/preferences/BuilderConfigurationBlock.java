/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.builder.preferences;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.internal.Activator;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.xtext.ui.preferences.ScrolledPageContent;

import com.google.common.collect.Lists;

/**
 * @author Michael Clay - Initial contribution and API
 * @since 2.1
 */
/*
 * TODO OUTPUT_FOLDERS it should be configurable which options for which output configuration are available,
 * e.g. there is almost no language where the "overwrite existing files" option 
 * should be changed by the user
 */
public class BuilderConfigurationBlock extends OptionsConfigurationBlock {
	private static final String SETTINGS_SECTION_NAME = "BuilderConfigurationBlock"; //$NON-NLS-1$

	private EclipseOutputConfigurationProvider configurationProvider;

	private List<TableItem> tableItems = Lists.newArrayList();

	public BuilderConfigurationBlock(IProject project, IPreferenceStore preferenceStore,
			EclipseOutputConfigurationProvider configurationProvider, IWorkbenchPreferenceContainer container) {
		super(project, preferenceStore, container);
		this.configurationProvider = configurationProvider;
	}

	@Override
	protected Control doCreateContents(Composite parent) {
		PixelConverter pixelConverter = new PixelConverter(parent);
		setShell(parent.getShell());
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainComp.setLayout(layout);
		Composite othersComposite = createBuildPathTabContent(mainComp);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.heightHint = pixelConverter.convertHeightInCharsToPixels(20);
		othersComposite.setLayoutData(gridData);
		validateSettings(null, null, null);
		return mainComp;
	}

	private Composite createBuildPathTabContent(Composite parent) {
		String[] trueFalseValues = new String[] { IPreferenceStore.TRUE, IPreferenceStore.FALSE };
		int columns = 3;
		final ScrolledPageContent pageContent = new ScrolledPageContent(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Composite composite = pageContent.getBody();
		composite.setLayout(layout);
		String label = Messages.BuilderConfigurationBlock_GeneralSection_Label;
		ExpandableComposite excomposite = createStyleSection(composite, label, columns);

		Composite othersComposite = new Composite(excomposite, SWT.NONE);
		excomposite.setClient(othersComposite);
		othersComposite.setLayout(new GridLayout(columns, false));

		addCheckBox(othersComposite, Messages.BuilderPreferencePage_GenerateAuto,
				BuilderPreferenceAccess.PREF_AUTO_BUILDING, trueFalseValues, 0);

		Set<OutputConfiguration> outputConfigurations = configurationProvider.getOutputConfigurations(getProject());

		for (OutputConfiguration outputConfiguration : outputConfigurations) {
			label = outputConfiguration.getDescription();
			excomposite = createStyleSection(composite, label, columns);
			othersComposite = new Composite(excomposite, SWT.NONE);
			excomposite.setClient(othersComposite);
			othersComposite.setLayout(new GridLayout(columns, false));
			Text defaultDirectoryField = addTextField(othersComposite, Messages.OutputConfigurationPage_Directory,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_DIRECTORY), 0, 200);
			addCheckBox(othersComposite, Messages.OutputConfigurationPage_CreateDirectory,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_CREATE_DIRECTORY), trueFalseValues, 0);
			addCheckBox(othersComposite, Messages.OutputConfigurationPage_OverrideExistingResources,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_OVERRIDE), trueFalseValues, 0);
			addCheckBox(othersComposite, Messages.OutputConfigurationPage_CreatesDerivedResources,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_DERIVED), trueFalseValues, 0);
			addCheckBox(othersComposite, Messages.OutputConfigurationPage_CleanupDerivedResources,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_CLEANUP_DERIVED), trueFalseValues, 0);
			addCheckBox(othersComposite, Messages.OutputConfigurationPage_CleanDirectory,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_CLEAN_DIRECTORY), trueFalseValues, 0);
			final Button installAsPrimaryButton = addCheckBox(othersComposite, Messages.BuilderConfigurationBlock_InstallDslAsPrimarySource,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.INSTALL_DSL_AS_PRIMARY_SOURCE), trueFalseValues, 0);
			final Button hideLocalButton = addCheckBox(othersComposite, Messages.BuilderConfigurationBlock_hideSyntheticLocalVariables,
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.HIDE_LOCAL_SYNTHETIC_VARIABLES), trueFalseValues, 0);
			hideLocalButton.setEnabled(installAsPrimaryButton.getSelection());
			installAsPrimaryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					hideLocalButton.setEnabled(installAsPrimaryButton.getSelection());
				}
			});
			GridData hideLocalButtonData = new GridData();
			hideLocalButtonData.horizontalIndent = 32;
			hideLocalButton.setLayoutData(hideLocalButtonData);
			addCheckBox(othersComposite, Messages.OutputConfigurationPage_KeepLocalHistory, 
					BuilderPreferenceAccess.getKey(outputConfiguration,
							EclipseOutputConfigurationProvider.OUTPUT_KEEP_LOCAL_HISTORY), trueFalseValues, 0);
			
			if (getProject() != null && !outputConfiguration.getSourceFolders().isEmpty()) {
				final Button outputPerSourceButton = addCheckBox(othersComposite,
						Messages.OutputConfigurationPage_UseOutputPerSourceFolder, BuilderPreferenceAccess.getKey(outputConfiguration,
								EclipseOutputConfigurationProvider.USE_OUTPUT_PER_SOURCE_FOLDER), trueFalseValues, 0);
				final Table table = createOutputFolderTable(othersComposite, outputConfiguration, defaultDirectoryField);
				table.setVisible(outputPerSourceButton.getSelection());
				outputPerSourceButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						table.setVisible(outputPerSourceButton.getSelection());
					}
				});
			}
		}
		registerKey(OptionsConfigurationBlock.IS_PROJECT_SPECIFIC);
		IDialogSettings section = Activator.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		restoreSectionExpansionStates(section);
		return pageContent;
	}

	private Table createOutputFolderTable(Composite othersComposite, final OutputConfiguration outputConfiguration, final Text defaultDirectoryField) {
		final Table table = new Table(othersComposite, SWT.BORDER | SWT.FULL_SELECTION);
		new TableColumn(table, SWT.NONE).setText(Messages.OutputConfigurationPage_IgnoreSourceFolder);
		new TableColumn(table, SWT.NONE).setText(Messages.OutputConfigurationPage_SourceFolder);
		new TableColumn(table, SWT.NONE).setText(Messages.OutputConfigurationPage_OutputDirectory);
		table.getColumn(0).setWidth(75);
		table.getColumn(1).setWidth(200);
		table.getColumn(2).setWidth(200);
		table.pack();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (final String source : outputConfiguration.getSourceFolders()) {
			String outputForSourceFolderKey = BuilderPreferenceAccess.getOutputForSourceFolderKey(outputConfiguration, source);
			registerKey(outputForSourceFolderKey);
			String ignoreSourceFolderKey = BuilderPreferenceAccess.getIgnoreSourceFolderKey(outputConfiguration, source);
			registerKey(ignoreSourceFolderKey);
			String defaultOutputDirectoryKey = BuilderPreferenceAccess.getKey(outputConfiguration,	EclipseOutputConfigurationProvider.OUTPUT_DIRECTORY);
			final TableItemData data = new TableItemData(source,outputForSourceFolderKey, ignoreSourceFolderKey, defaultOutputDirectoryKey);
			
			final TableItem item = new TableItem(table, SWT.NONE, 0);
			tableItems.add(item);
			item.setData(data);
			refreshItem(item);

			final TableEditor directoryEditor = new TableEditor(table);
			directoryEditor.grabHorizontal = true;
			table.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.item != item)
						return;
					if (isIgnored(item))
						return;
					Control oldDirectoryField = directoryEditor.getEditor();
					if (oldDirectoryField != null) {
						oldDirectoryField.dispose();
					}

					final Text directoryField = new Text(table, SWT.NONE);
					directoryField.setText(getOutputDirectory(item));
					directoryField.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent me) {
							setValue(data.getOutputDirectoryKey(), directoryField.getText());
							refreshItem(item);
						}
					});
					directoryField.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent e) {
							directoryField.dispose();
						}
					});
					directoryField.selectAll();
					directoryField.setFocus();
					directoryEditor.setEditor(directoryField, item, 2);
				}
			});
			final TableEditor ignoreEditor = new TableEditor(table);
			ignoreEditor.grabHorizontal = true;
			final Button ignoreField = new Button(table, SWT.CHECK);
			ignoreEditor.setEditor(ignoreField, item, 0);
			ignoreField.setSelection(isIgnored(item));
			ignoreField.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setValue(data.getIgnoreKey(),
							String.valueOf(ignoreField.getSelection()));
					refreshItem(item);
				}
			});
			defaultDirectoryField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					refreshItem(item);
				}
			});
		}
		return table;
	}
	
	private void refreshItem(final TableItem item) {
		TableItemData data = (TableItemData) item.getData();
		item.setText(1, data.getSourceFolder());
		String outputDirectory = getOutputDirectory(item);
		if ("".equals(outputDirectory)) {
			item.setForeground(2, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			item.setText(2, getValue(data.getDefaultOutputDirectoryKey()));
		} else {
			item.setForeground(2, null);
			item.setText(2, outputDirectory);
		}
		if (isIgnored(item)) {
			item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		} else {
			item.setForeground(null);
		}
	}
	
	private String getOutputDirectory(TableItem item) {
		TableItemData data = (TableItemData) item.getData();
		return getValue(data.getOutputDirectoryKey());
	}
	
	private boolean isIgnored(TableItem item)  {
		TableItemData data = (TableItemData) item.getData();
		return Boolean.parseBoolean(getValue(data.getIgnoreKey()));
	}
	
	private static class TableItemData {
		private String sourceFolder;
		private String outputDirectoryKey;
		private String ignoreKey;
		private String defaultOutputDirectoryKey;
		
		public TableItemData(String sourceFolder, String outputDirectoryKey, String ignoreKey, String defaultOutputDirectoryKey) {
			this.sourceFolder = sourceFolder;
			this.outputDirectoryKey = outputDirectoryKey;
			this.ignoreKey = ignoreKey;
			this.defaultOutputDirectoryKey = defaultOutputDirectoryKey;
		}
		
		public String getSourceFolder() {
			return sourceFolder;
		}
		
		public String getOutputDirectoryKey() {
			return outputDirectoryKey;
		}
		
		public String getIgnoreKey() {
			return ignoreKey;
		}
		
		public String getDefaultOutputDirectoryKey() {
			return defaultOutputDirectoryKey;
		}
		
	}
	
	@Override
	protected void validateSettings(String changedKey, String oldValue, String newValue) {
	}
	
	@Override
	protected void updateControls() {
		super.updateControls();
		for (TableItem item : tableItems) {
			refreshItem(item);
		}
	}

	@Override
	public void dispose() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION_NAME);
		storeSectionExpansionStates(settings);
		super.dispose();
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title = Messages.BuilderConfigurationBlock_SettingsChanged_Title;
		String message;
		if (workspaceSettings) {
			message = Messages.BuilderConfigurationBlock_SettingsChanged_WorkspaceBuild;
		} else {
			message = Messages.BuilderConfigurationBlock_SettingsChanged_ProjectBuild;
		}
		return new String[] { title, message };
	}

	@Override
	protected Job getBuildJob(IProject project) {
		Job buildJob = new OptionsConfigurationBlock.BuildJob(Messages.BuilderConfigurationBlock_BuildJob_Title0, project);
		buildJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
		buildJob.setUser(true);
		return buildJob;
	}

}
