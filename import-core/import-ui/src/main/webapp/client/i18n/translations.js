module.exports = {
	'resources' : {
		'en' : {
			translation : {
				'header' : {
					'import_tool' : 'Import Tool',
					'nav' : {
						'menu' : {
							'home' : ' Home ',
							'import_process' : ' Import Process ',
							'reports' : ' Reports ',
							'close' : ' Close ',
							'language_selector':' Language',
							'submenu' : {
								'iati104' : 'IATI 1.04 to AMP',
								'iati105' : 'IATI 1.05 to AMP',
								'iati201' : 'IATI 2.01 to AMP',
								'logs' : 'Logs',
								'previous_imports' : 'Previous Imports',
								'en':'English ',
								'fr':'French ',
								'workflow_list': 'Workflow List'
								 
							}
						}
					}
				},
				'wizard' : {
					'import_process' : 'Import Process',
					'steps' : {
						'upload_files' : ' Upload File(s)',
						'filter_data' : ' Filter Data',
						'choose_projects' : ' Choose Projects',
						'choose_fields' : ' Choose Fields',
						'map_values' : ' Map Values',
						'review_import' : ' Review and Import'
					},
					'upload_file' : {
						'select_file' : 'Select files to upload',
						'filename' : 'Filename',
		                'valid' : 'Valid',
		                'invalid' : 'Invalid',
						'upload_date' : 'Date and Time',
						 'action':'Action',
						'view' : 'View',
						'next' : 'Next >>',
						'previous' : '<< Previous',
						 'msg_file_exists': ' File with same name already exists ',
						 'msg_error_retrieving_files': 'Error retrieving uploaded files',
					     'msg_invalid_file': 'The file seems to be invalid. Please check the file\'s IATI version.'
					},
					'filter_data' : {
						'filter_information': 'Filter Information',
						'select_filters' : 'Select for each field, which values you would like to include as part of the import process',
						'language' : 'Language',
						'next' : 'Next >>',
						'previous' : '<< Previous',
						'msg_error_retrieving_languages': 'Error retrieving languages.',
						'msg_error_retrieving_filters': ' Error retrieving filters.'
						
					},
					'choose_projects' : {
						'choose_projects' : 'Choose Projects',
						'new_projects' : 'New Projects',
						'existing_projects' : 'Existing Projects',						
						'import' : 'Import',
						'source_project' : 'Source Project',
						'destination_project' : 'Destination Project',
						'update' : 'Update',
						'next' : 'Next >>',
						'previous' : '<< Previous',
						'msg_error_retrieving_projects': ' Error retrieving filters.',
						'msg_error_select_project': ' Please select at least one project to proceed'
					},
					'map_fields' : {
						'choose_map_fields' : 'Choose and Map Fields',
						'import_update' : 'Import/Update',
						'source_field' : 'Source Field',
						'destination_field' : 'Destination Field',
						'next' : 'Next >>',
						'save' : 'Save',
						'load_existing_template' : 'Load Existing Template',
						'usual_field_mapping' : 'Usual Field Mapping',
						'other_field_mapping' : 'Other Field Mapping',
						'previous' : '<< Previous',
						'multilang_string': 'Multi-language Fields',
					    'string': 'String Fields',
					    'list':'List Fields',
					    'date': 'Date Fields',
					    'transaction': 'Transaction Fields',
					    'organization': 'Orgarnization Fields',
					    'msg_error_retrieving_destination_fields': ' Error retrieving destination fields.',
					    'msg_error_retrieving_source_fields': ' Error retrieving source fields.',
					    'msg_error_retrieving_mappings': ' Error retrieving field mappings.',
					    'msg_error_retrieving_templates': ' Error loading mapping templates'					    
					},
					'map_values' : {
						'map_field_values' : 'Map Fields Values',
						'empty_list' : 'No mappable fields',
						'save' : 'Save',
						'update' : 'Update',
						'next' : 'Next >>',
						 'source_value':'Source Value',
						 'destination_value':'Destination Value',
						 'previous' : '<< Previous',
						 'msg_error_retrieving_value_mappings': ' Error retrieving value mappings',
						 'msg_error_loading_templates': ' Error loading mapping templates'
					},
					'save_field_mappings_dlg': {
					    'msg_error_saving': 'Error saving template',
					    'title': 'Save Field Mappings',
					    'close': 'close',
					    'save_mapping': 'Save Mapping'
					  
					},
					'save_value_mappings_dlg':{
					  'msg_error_saving': 'Error saving template',
					  'title': 'Save Value Mappings',
					  'close': 'close',
					  'save_mapping': 'Save Mapping'	
					},
					'review_import' : {
						'review_import' : 'Review and Import',
						'files_uploaded' : '__fileCount__ File(s) Uploaded',
						'data_filtered' : '__filterCount__ Filter(s) Applied',
						'projects_selected' : ' __projectCount__ Project(s) Selected',
						'fields_selected' : '__fieldMappingCount__ Field(s) Selected',
						'values_mapped' : '__valueMappingCount__ Value(s) Mapped',
						'proceed_import' : 'Proceed with Import',
						'close' : 'Close ',
						'restart' : 'Restart',
						'previous' : '<< Previous',
						'question' :  'Are you sure you want to restart the process?'
					},
					'mappings_dropdown':{
						'confirm_delete': 'Are you sure you want to delete  __templateName__ ?'
					}
				}
			}
		},
		'fr' : {
			translation : {
				'header' : {
					'import_tool' : 'Outil d\'importation',
					'nav' : {
						'menu' : {
							'home' : ' Accueil ',
							'import_process' : ' Processus D\'importation ',
							'reports' : ' Rapports ',
							'close' : ' Fermer ',
							'language_selector':' Language',
							'submenu' : {
								'iati104' : 'IATI 1.04 to AMP',
								'iati105' : 'IATI 1.05 à AMP',
								'iati201' : 'IATI 2.01 à AMP',
								'logs' : 'Logs',
								'previous_imports' : 'Importations Précédent',
								'en':'English ',
								'fr':'French '
							}
						}
					}
				},
				'wizard' : {
					'import_process' : 'Processus D\'importation',
					'steps' : {
						'upload_files' : ' Télécharger Des Fichiers',
						'filter_data' : ' Filtrer les données',
						'choose_projects' : ' Choisissez Projets',
						'choose_fields' : ' Choisissez champs',
						'map_values' : ' Carte Valeurs',
						'review_import' : ' Examen et à l\'importation'
					},
					'upload_file' : {
						'select_file' : 'Sélectionnez les fichiers à télécharger',
						'filename' : 'Nom De Fichier',
						'upload_date' : 'Date et heure',
						'action':'Action',
						'view' : 'Vue',
						'next' : 'Suivant >>',
						'previous' : '<< Previous',
						 'msg_file_exists': ' File with same name already exists ',
						 'msg_error_retrieving_files': 'Error retrieving uploaded files',
						 'msg_invalid_file': 'The file seems to be invalid. Please check the file\'s IATI version.'
					},
					'filter_data' : {
						'filter_information': 'Filtrer l\'information',
						'select_filters' : 'Sélectionnez pour chaque champ , les valeurs que vous souhaitez inclure dans le cadre du processus d\'importation',
						'language' : 'Langue',
						'next' : 'Suivant >>',
						'previous' : '<< Previous',
						'msg_error_retrieving_languages': 'Error retrieving languages.',
						'msg_error_retrieving_filters': ' Error retrieving filters.'
					},
					'choose_projects' : {
						'choose_projects' : 'Choisissez Projets',
						'new_projects' : 'Nouveaux Projets',
						'existing_projects' : 'Projets Existants',
						'import' : 'Importation',
						'source_project' : 'Projet de Source',
						'destination_project' : 'Destination Project',
						'update' : 'Mise À Jour',
						'next' : 'Suivant >>',
						'previous' : '<< Previous',
						'msg_error_retrieving_projects': ' Error retrieving filters.',
						'msg_error_select_project': ' Please select at least one project to proceed'
					},
					'map_fields' : {
						'choose_map_fields' : 'Choisissez et Carte champs',
						'import_update' : 'Import / Mise à jour',
						'source_field' : 'Champ Source',
						'destination_field' : 'Field Destinations',
						'next' : 'Suivant >>',
						'save' : 'Sauvegarder',
						'load_existing_template' : 'Charge modèle existant',
						'usual_field_mapping' : 'Correspondance des champs d\'habitude',
						'other_field_mapping' : 'Autre Correspondance des champs',
						'previous' : '<< Previous',
						'multilang_string': 'Les champs multi-langues',
					    'string': 'Les champs à Cordes',
					    'list':'Liste champs',
					    'date': 'Champs de date',
					    'transaction': 'Transaction champs',
					    'organization': 'Orgarnization champs',
					    'msg_error_retrieving_destination_fields': ' Error retrieving destination fields.',
					    'msg_error_retrieving_source_fields': ' Error retrieving source fields.',
					    'msg_error_retrieving_mappings': ' Error retrieving field mappings.',
					    'msg_error_retrieving_templates': ' Error loading mapping templates'
						
					},
					'map_values' : {
						'map_field_values' : 'Plan valeurs des champs',
						'empty_list' : 'No mappable fields',
						'save' : 'Sauvegarder',
						'update' : 'Mise À Jour',
						'next' : 'Suivant >>',
						'source_value':'Valeur Source',
						'destination_value':'Destinations Valeur',
						'previous' : '<< Previous',
						'msg_error_retrieving_value_mappings': ' Error retrieving value mappings',
						'msg_error_loading_templates': ' Error loading mapping templates'
					},
					'review_import' : {
						'review_import' : 'Examen et à l\'importation',
						'files_uploaded' : 'Fichiers Téléchargés',
						'data_filtered' : 'Données Filtrées',
						'projects_selected' : 'Les projets sélectionnés',
						'fields_selected' : 'Champs Sélectionnés',
						'values_mapped' : 'Valeurs Mappées',
						'proceed_import' : 'Procéder à l\'importation',
						'close' : 'Fermer ',
		                'restart' : 'Restart',
						'previous' : '<< Previous',
						'question' :  'Are you sure you want to restart the process?'
					},
					'save_field_mappings_dlg': {
					    'msg_error_saving': 'Error saving template',
					    'title': 'Save Field Mappings',
					    'close': 'close',
					    'save_mapping': 'Save Mapping'
					  
					},
					'save_value_mappings_dlg':{
					  'msg_error_saving': 'Error saving template',
					  'title': 'Save Value Mappings',
					  'close': 'close',
					  'save_mapping': 'Save Mapping'	
					},
					'mappings_dropdown':{
						'confirm_delete': 'Are you sure you want to delete  __templateName__ ?'
					}
				}
			}
		}

	}
};
