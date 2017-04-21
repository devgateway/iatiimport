module.exports = {
		'resources' : {
			'en' : {
				translation : {
					'header' : {
						'import_tool' : 'Import Tool',
						'select_import_process': 'Please select the import process:',
						'close_window': 'Close Import Tool Window?',
						'nav' : {
							'menu' : {
								'home' : ' Home ',
								'import_process' : ' Import Process ',
								'reports' : ' Reports ',
								'close' : ' Close ',
								'language_selector' : ' Language',
								'submenu' : {
									'iati101': 'IATI 1.01 to AMP',
									'iati103' : 'IATI 1.03 to AMP',
									'iati104' : 'IATI 1.04 to AMP',
									'iati105' : 'IATI 1.05 to AMP',
									'iati201' : 'IATI 2.01 to AMP',
									'iati202' : 'IATI 2.02 to AMP',
									'to':'to',
									'logs' : 'Logs',
									'previous_imports' : 'Previous Imports',
									'en' : 'English ',
									'fr' : 'French ',
									 'es': 'Spanish',
									'workflow_list' : 'Workflow List'

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
							'action' : 'Action',
							'view' : 'View',
							'next' : 'Next >>',
							'previous' : '<< Previous',
							'msg_file_exists' : ' File with same name already exists ',
							'msg_error_retrieving_files' : 'Error retrieving uploaded files',
							'msg_error_upload_failed' : 'Error uploading file. Please check that the file size does not exceed 2MB',
							'msg_invalid_file' : 'The file seems to be invalid. Please check the file\'s IATI version.'
						},
						'filter_data' : {
							'filter_information' : 'Filter Information',
							'select_filters' : 'Select for each field, which values you would like to include as part of the import process',
							'language' : 'Language Selection for Text Fields',
							'next' : 'Next >>',
							'previous' : '<< Previous',
							'msg_error_retrieving_languages' : 'Error retrieving languages.',
							'msg_error_retrieving_filters' : ' Error retrieving filters.'

						},
						'choose_projects' : {
							'choose_projects' : 'Choose Projects',
							'new_projects' : 'New Projects',
							'existing_projects' : 'Existing Projects',
							'import' : 'Import',
							'source_project' : 'Source Project',
							'iati_id': 'IATI ID',
							'destination_project' : 'Destination Project',
							'override_title': 'Override Title',
							'update' : 'Update',
							'next' : 'Next >>',
							'previous' : '<< Previous',
							'msg_error_retrieving_projects' : ' Error retrieving filters.',
							'msg_error_select_project' : ' Please select at least one project to proceed',
						    'msg_project_not_editable':'Projects marked with an * and highlighted are not editable because they are not in the current workspace.'
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
							'multilang_string' : 'Multi-language Fields',
							'string' : 'String Fields',
							'list' : 'List Fields',
							'date' : 'Date Fields',
							'transaction' : 'Transaction Fields',
							'organization' : 'Orgarnization Fields',
							'msg_error_retrieving_destination_fields' : ' Error retrieving destination fields.',
							'msg_error_retrieving_source_fields' : ' Error retrieving source fields.',
							'msg_error_retrieving_mappings' : ' Error retrieving field mappings.',
							'msg_error_retrieving_templates' : ' Error loading mapping templates',
							'msg_required_field':' is required by the destination system.',
							'msg_field_has_dependencies': ' has the following dependencies: __dependencies__ '

						},
						'map_values' : {
							'map_field_values' : 'Map Fields Values',
							'empty_list' : 'No mappable fields',
							'save' : 'Save',
							'update' : 'Update',
							'next' : 'Next >>',
							'source_value' : 'Source Value',
							'destination_value' : 'Destination Value',
							'previous' : '<< Previous',
							'msg_error_retrieving_value_mappings' : ' Error retrieving value mappings',
							'msg_error_loading_templates' : ' Error loading mapping templates'
						},
						'save_field_mappings_dlg' : {
							'msg_error_saving' : 'Error saving template',
							'title' : 'Save Field Mappings',
							'close' : 'close',
							'save_mapping' : 'Save Mapping'

						},
						'save_value_mappings_dlg' : {
							'msg_error_saving' : 'Error saving template',
							'title' : 'Save Value Mappings',
							'close' : 'close',
							'save_mapping' : 'Save Mapping'
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
							'question' : 'Are you sure you want to restart the process?'
						},
						'mappings_dropdown' : {
							'confirm_delete' : 'Are you sure you want to delete  __templateName__ ?'
						},
						'previous_imports_list' :{
							'id':'ID',
							'previous_imports': 'Previous Imports',
							'file_name':'File Name',
							'upload_date': 'Upload Date',
							'action': 'Action',
							'view_import': 'View Import',
							'delete': 'Delete'		 
						},
						'select': 'Select',
						'import_report':{
							'import_process': 'Import Process',
							'project_id': 'Project Id',
							'operation': 'Operation',
							'status': 'Status',
							'message': 'Message/Project Title',
							'close': 'Close'
						}
					},
				  'server_messages': {
					  101: 'Extracting project __processed__ of __total__',
					  102: 'Fetching destination projects',
					  103: 'An error occurred while extracting projects from the IATI file. Please check the file format',
					  104: 'Error uploading file. Check if the initial steps are done.',
					  105: 'Error uploading file.',
					  106: 'Importing __processed__ of __total__ projects',
					  107: 'Mapping __processed__ of __total__'					  
				  },
				  'previous_imports' : {
					   'title': 'Previous Imports',
					   'file_name': 'File Name',
					   'upload_date': 'Upload Date',
					   'action': 'Action',
					   'view_import': 'View Import',
					   'delete': 'Delete'
					},
					'workflows':{
						'title':'Workflows',
						'source': 'Source Processor',
						'destination': 'Destination Processor',
						'description': 'Description',
						'enabled': 'Enabled'
					}
				}
			},	
			'es' : {
				translation : {
					'header' : {
						'import_tool' : 'Herramienta de Importación',
						'select_import_process': 'Por favor seleccione el proceso de importación:',
						'close_window': 'Cerrar ventana de importación?',
						'nav' : {
							'menu' : {
								'home' : ' Página principal ',
								'import_process' : ' Proceso de importación ',
								'reports' : ' Reportes ',
								'close' : ' Cerrar ',
								'language_selector' : ' Idioma',
								'submenu' : {
									'iati101': 'IATI 1.01 hacia AMP',
									'iati103' : 'IATI 1.03 hacia AMP',
									'iati104' : 'IATI 1.04 hacia AMP',
									'iati105' : 'IATI 1.05 hacia AMP',
									'iati201' : 'IATI 2.01 hacia AMP',
									'iati202' : 'IATI 2.02 hacia AMP',
									'to':'hacia',
									'logs' : 'Registro',
									'previous_imports' : 'Importaciones Previas',
									'en' : 'Inglés ',
									'fr' : 'Francés ',
									'es':'Spanish ',
									'workflow_list' : 'Lista de Flujos de Trabajo'

								}
							}
						}
					},
					'wizard' : {
						'import_process' : 'Proceso de Importación',
						'steps' : {
							'upload_files' : ' Cargar Archivo(s)',
							'filter_data' : ' Filtrar Datos',
							'choose_projects' : ' Seleccionar Proyectos',
							'choose_fields' : ' Seleccionar Campos',
							'map_values' : ' Mapeo de Valores',
							'review_import' : 'Verificar e Importar'
						},
						'upload_file' : {
							'select_file' : 'Seleccionar archivos a cargar',
							'filename' : 'Nombre del Archivo',
							'valid' : 'Valido',
							'invalid' : 'Inválido',
							'upload_date' : 'Fecha y Hora',
							'action' : 'Acción',
							'view' : 'Ver',
							'next' : 'Siguiente >>',
							'previous' : '<< Anterior',
							'msg_file_exists' : ' Un archivo con el mismo nombre ya existe',
							'msg_error_retrieving_files' : 'Error al recuperar archivos cargados',
							'msg_error_upload_failed' : 'Error al cargar el archivo. Compruebe que el tamaño del archivo no excede 2 MB',
							'msg_invalid_file' : 'El archivo parece ser invalido. Por favor verifique la versión IATI del archivo the file.'
						},
						'filter_data' : {
							'filter_information' : 'Información de Filtros',
							'select_filters' : 'Seleccione para cada campo los valores que desea incluir como parte del proceso de importación',
							'language' : 'Selección de Idioma para los Campos de Texto',
							'next' : 'Siguiente >>',
							'previous' : '<< Anterior',
							'msg_error_retrieving_languages' : 'Error al recuperar los idiomas.',
							'msg_error_retrieving_filters' : ' Error al recuperar los filtros.'

						},
						'choose_projects' : {
							'choose_projects' : 'Seleccionar Proyectos',
							'new_projects' : 'Proyectos Nuevos',
							'existing_projects' : 'Proyectos existentes',
							'import' : 'Importar',
							'source_project' : 'Proyecto Fuente',
							'iati_id': 'ID de IATI',
							'destination_project' : 'Proyecto de Destino',
							'override_title': 'Invalidar el Título',
							'update' : 'Actualizar',
							'next' : 'Siguiente >>',
							'previous' : '<< Anterior',
							'msg_error_retrieving_projects' : ' Error al recuperar los filtros.',
							'msg_error_select_project' : ' Favor seleccionar al menos un proyecto para continuar',
						    'msg_project_not_editable':'Los proyectos marcador por un * y resaltados no pueden ser editados puesto que no encuentran en el espacio de trabajo actual.'
						},
						'map_fields' : {
							'choose_map_fields' : 'Seleccionar y mapear los campos',
							'import_update' : 'Importar/Actualizar',
							'source_field' : 'Campo de origen',
							'destination_field' : 'Campo de destino',
							'next' : 'Siguiente >>',
							'save' : 'Guardar',
							'load_existing_template' : 'Cargar plantilla existente',
							'usual_field_mapping' : 'Mapeo de campo habitual',
							'other_field_mapping' : 'Otro Mapeo de Campo',
							'previous' : '<< Anterior',
							'multilang_string' : 'Campos con idiomas multiples',
							'string' : 'Camps de Cadenas',
							'list' : 'Lista de Campos',
							'date' : 'Campos de Fecha',
							'transaction' : 'Campos de Transacciones',
							'organization' : 'Campos de Orgarnizaciones',
							'msg_error_retrieving_destination_fields' : ' Error al recuperar los campos de destino.',
							'msg_error_retrieving_source_fields' : ' Error al recuperar los campos de origen.',
							'msg_error_retrieving_mappings' : ' Error al recuperar el mapeo de los campos.',
							'msg_error_retrieving_templates' : ' Error al recuperar la plantilla de mapeo',
							'msg_required_field':' es requerido por el sistema de destino.',
							'msg_field_has_dependencies': ' tiene las siguientes dependencias: __dependencies__ '

						},
						'map_values' : {
							'map_field_values' : 'Mapear los Campos de Valores',
							'empty_list' : 'No hay campos que puedan ser mapeados',
							'save' : 'Guardar',
							'update' : 'Actualizar',
							'next' : 'Siguiente >>',
							'source_value' : 'Valor de Origen',
							'destination_value' : 'Valor de Destino',
							'previous' : '<< Anterior',
							'msg_error_retrieving_value_mappings' : ' Error al recuperar valores de mapeo',
							'msg_error_loading_templates' : ' Error al cargan las plantillas de mapeo'
						},
						'save_field_mappings_dlg' : {
							'msg_error_saving' : 'Error al guardar la plantilla',
							'title' : 'Guardar el Mapeo de los Campos',
							'close' : 'cerrar',
							'save_mapping' : 'Guardar Mapeo'

						},
						'save_value_mappings_dlg' : {
							'msg_error_saving' : 'Error al guardar la plantilla',
							'title' : 'Guardar los Valores de Mapeo',
							'close' : 'cerrar',
							'save_mapping' : 'Guardar Mapeo'
						},
						'review_import' : {
							'review_import' : 'Revisar e Importar',
							'files_uploaded' : '__fileCount__ Archivo(s) Cargado(s)',
							'data_filtered' : '__filterCount__ Filtro(s) Aplicado(s)',
							'projects_selected' : ' __projectCount__ Proyecto(s) Seleccionado(s)',
							'fields_selected' : '__fieldMappingCount__ Campo(s) Seleccionado(s)',
							'values_mapped' : '__valueMappingCount__ Valore(s) Mapeado(s)',
							'proceed_import' : 'Proceder con la Importación',
							'close' : 'Cerrar ',
							'restart' : 'Reiniciar',
							'previous' : '<< Anterior',
							'question' : 'Está seguro de reiniciar el proceso?'
						},
						'mappings_dropdown' : {
							'confirm_delete' : 'Está seguro de eliminar  __templateName__ ?'
						},
						'previous_imports_list' :{
							'id':'ID',
							'previous_imports': 'Importaciones Previas',
							'file_name':'Nombre del Archivo',
							'upload_date': 'Fecha de Carga',
							'action': 'Acción',
							'view_import': 'Ver Importación',
							'delete': 'Eliminar'		 
						},
					    'select': 'Seleccionar',						
						'import_report':{
							'import_process': 'Proceso de Importación',
							'project_id': 'ID Proyecto',
							'operation': 'Operación',
							'status': 'Estado',
							'message': 'Mensaje / Nombre del Proyecto',
							'close': 'Cerrar'
						}
					},
				  'server_messages': {
					  101: 'Extrayendo proyectos __processed__ of __total__',
					  102: 'Obteniendo proyectos de destino',
					  103: 'Se produjo un error al extraer proyectos del archivo IATI. Favor revisar el formato del archivo',
					  104: 'Error al cargar el archivo. Verificar que los primeros pasos hayan sido completados.',
					  105: 'Error al cargar el archivo.',
					  106: 'Importando __processed__ proyecto de __total__',
					  107: 'Mapping __processed__ of __total__'					  
				  },
				  'previous_imports' : {
					   'title': 'Importaciones previas',
					   'file_name': 'Nombre del archivo',
					   'upload_date': 'Fecha de importación',
					   'action': 'Acción',
					   'view_import': 'Ver importación',
					   'delete': 'Eliminar'
					},
					'workflows':{
						'title':'Flujos de importación',
						'source': 'Procesador de la fuente',
						'destination': 'Procesador de destino',
						'description': 'Descripción',
						'enabled': 'Habilitado'
					}
				}
			},		
			'fr' : {
				translation : {
					'header' : {
						'import_tool' : 'Outil Import',
						'select_import_process' : 'Veuillez sélectionner le processus d’import des données:',
						'close_window': 'Close Import Tool Window?',
						'nav' : {
							'menu' : {
								'home' : ' Accueil ',
								'import_process' : ' Processus ',
								'reports' : ' Rapports ',
								'close' : ' Fermer ',
								'language_selector' : ' Langue',
								'submenu' : {
									'iati101': 'IATI 1.01 vers AMP',
									'iati103' : 'IATI 1.03 vers AMP',
									'iati104' : 'IATI 1.04 vers AMP',
									'iati105' : 'IATI 1.05 vers AMP',
									'iati201' : 'IATI 2.01 vers AMP',
									'iati202' : 'IATI 2.02 vers AMP',
									 'to':'vers',
									'logs' : 'Logs',
									'previous_imports' : 'Téléchargements Précédents',
									'en' : 'English ',
									'fr' : 'Français ',
									'es': 'Spanish',
									'workflow_list' : 'Flux de téléchargement'

								}
							}
						}
					},
					'wizard' : {
						'import_process' : 'Processus de Téléchargement',
						'steps' : {
							'upload_files' : ' Télécharger le(s)fichier(s)',
							'filter_data' : ' Filtrer les données',
							'choose_projects' : ' Choisir les projets',
							'choose_fields' : ' Choisir les champs',
							'map_values' : ' Faire correspondre les valeurs',
							'review_import' : ' Revoir et télécharger'
						},
						'upload_file' : {
							'select_file' : 'Sélectionner les fichiers à télécharger files',
							'filename' : 'Nom du fichier',
							'valid' : 'Correct',
							'invalid' : 'Incorrect',
							'upload_date' : 'Date et Heure',
							'action' : 'Action',
							'view' : 'Visualiser',
							'next' : 'Suivant >>',
							'previous' : '<< Précédent',
							'msg_file_exists' : ' Un fichier du meme nom existe déjà',
							'msg_error_retrieving_files' : 'L’outil ne retrouve pas les fichiers téléchargés',
							'msg_error_upload_failed' : 'L’outil ne parvient pas à télécharger le fichier. Veuillez verifier que la taille du fichier n’est pas supérieure à 2MB',
							'msg_invalid_file' : 'Le fichier semble être incorrect. Veuillez verifier la version IATI du fichier.'
						},
						'filter_data' : {
							'filter_information' : 'Information  Filtre',
							'select_filters' : 'Sélectionner les valeurs à inclure dans le processus de téléchargement pour chaque champ.',
							'language' : 'Sélectionner la langue des champs Texte',
							'next' : 'Suivant >>',
							'previous' : '<< Précédent',
							'msg_error_retrieving_languages' : 'Erreur dans la recherche des langues disponibles.',
							'msg_error_retrieving_filters' : ' Erreur dans la recherche des Filtres'

						},
						'choose_projects' : {
							'choose_projects' : ' Choisir les projets ',
							'new_projects' : ' Nouveaux Projets ',
							'existing_projects' : ' Projets Existants ',
							'import' : ' Télécharger ',
							'source_project' : ' Projet Source ',
							'iati_id': 'IATI ID',
							'destination_project' : ' Projet de Destination ',
							'override_title': 'Ecraser le titre du projet',
							'update' : ' Mettre à jour ',
							'next' : ' Suivant >> ',
							'previous' : ' << Précédent ',
							'msg_error_retrieving_projects' : ' Erreur dans la recherche des Filtres.',
							'msg_error_select_project' : ' Veuillez sélectionner au moins un projet pour continuer ',
							'msg_project_not_editable': 'Les projets ayant un * rouge ne peuvent pas être modifiés parce qu’ils ne sont pas inscrits dans cet espace de travail'
						},
						'map_fields' : {
							'choose_map_fields' : 'Choisir et faire correspondre les champs',
							'import_update' : 'Télécharger/Mettre à jour',
							'source_field' : 'Champ Source',
							'destination_field' : 'Champ de Destination',
							'next' : 'Suivant >>',
							'save' : 'Enregistrer',
							'load_existing_template' : 'Télécharger le modèle existant',
							'usual_field_mapping' : 'Usual Field Mapping',
							'other_field_mapping' : 'Other Field Mapping',
							'previous' : '<< Précédent',
							'multilang_string' : 'Champs Multi-langue ',
							'string' : 'Champ Texte',
							'list' : 'Champs Liste',
							'date' : 'Champs Date',
							'transaction' : 'Champs Transaction',
							'organization' : 'Champs Organisations',
							'msg_error_retrieving_destination_fields' : ' Erreur dans la recherche des champs destination.',
							'msg_error_retrieving_source_fields' : ' Erreur dans la recherche des champs Source.',
							'msg_error_retrieving_mappings' : ' Erreur dans la correspondence des champs.',
							'msg_error_retrieving_templates' : 'Erreur dans le téléchargement des modèles de correspondance ',
							'msg_required_field':' un paramètre obligatoire.',
							'msg_field_has_dependencies': ' est attaché à: __dependencies__ '
						},
						'map_values' : {
							'map_field_values' : ' Correspondance Valeurs ',
							'empty_list' : ' Aucun champ correspondant ',
							'save' : ' Enregistrer ',
							'update' : ' Mettre à jour ',
							'next' : ' Suivant >> ',
							'source_value' : ' Valeur Source ',
							'destination_value' : ' Valeur Destination ',
							'previous' : ' << Précédent ',
							'msg_error_retrieving_value_mappings' : ' Erreur dans la correspondence des valeurs ',
							'msg_error_loading_templates' : ' Erreur dans le téléchargement des modèles de correspondance '
						},
						'save_field_mappings_dlg' : {
							'msg_error_saving' : ' Erreur dans la sauvegarde du modèle ',
							'title' : ' Enregistrer la correspondance des champs ',
							'close' : ' Fermer ',
							'save_mapping' : ' Enregistrer la correspondance '
						},
						'save_value_mappings_dlg' : {
							'msg_error_saving' : ' Erreur dans la sauvegarde du modèles ',
							'title' : ' Enregistrer la correspondence des valeurs ',
							'close' : ' Fermer ',
							'save_mapping' : ' Enregistrer la correspondance '
						},
						'review_import' : {
							'review_import' : ' Revoir et Télécharger ',
							'files_uploaded' : ' __fileCount__ Fichiers(s)Uploaded ',
							'data_filtered' : ' __filterCount__ Filtre(s)Appliqués ',
							'projects_selected' : ' __projectCount__ Projet(s)Sélectionnés ',
							'fields_selected' : ' __fieldMappingCount__ Champ(s)Sélectionné(s)',
							'values_mapped' : ' __valueMappingCount__ Valeurs(s)Correspondantes ',
							'proceed_import' : ' Continuer avec le téléchargement ',
							'close' : ' Fermer ',
							'restart' : ' Réinitialiser ',
							'previous' : ' << Précédent ',
							'question' : ' Etes - vous sûr(e)de vouloir recommencer le processus de téléchargement ? '
						},
						'mappings_dropdown' : {
							'confirm_delete' : ' Etes - vous sûr(e)de vouloir supprimer __templateName__ ? '
						},
						'previous_imports_list' :{
							'id':' ID ',
							'previous_imports': ' Téléchargements Précédents ',
							'file_name':' Nom du fichier ',
							'upload_date': ' Date de Téléchargement ',
							'action': ' Action ',
							'view_import': ' Visualiser le Téléchargement ',
							'delete': ' Supprimer '
						},
						'select': 'Select',
						'import_report':{
							'import_process': 'Import Process',
							'project_id': 'Project Id',
							'operation': 'Operation',
							'status': 'Status',
							'message': 'Message/Project Title',
							'close': 'Close'
						},
					},
					'server_messages': {
						  101: 'Extraction de __processed__ sur __total___',
						  102: 'Chargement des projets',
						  103: "Un erreur s'est produite dans le processus d'extraction des projets IATI. Veuillez vérifier le format du fichier.",
						  104: "Une erreur s'est produite dans le téléchargement du fichier. Veuillez vérifier les étapes initiales.",
						  105: "Une erreur s'est produite dans le téléchargement du fichier.",
						  106: 'Chargement de __processed__ sur __total__ projets',
						  107: 'Correspondance  __processed__ sur __total__ projets'				  
					  },
					  'previous_imports' : {
						   'title': 'Previous Imports',
						   'file_name': 'File Name',
						   'upload_date': 'Upload Date',
						   'action': 'Action',
						   'view_import': 'View Import',
						   'delete': 'Delete'
						},
						'workflows':{
							'title':'Workflows',
							'source': 'Source Processor',
							'destination': 'Destination Processor',
							'description': 'Description',
							'enabled': 'Enabled'
						}
				}		
			}

		}
};
