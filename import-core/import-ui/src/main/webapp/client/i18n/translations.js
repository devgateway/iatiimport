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
								'admin': 'Admin',
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
									'workflow_list' : 'Workflow List',
									'data_source' : 'Data Source Manager'									
								}
							}
						}
					},
					'wizard' : {
						'next' : 'Next >>',
						'previous' : '<< Previous',
						'import_process' : 'Import Process',
						'invalid_session': 'Invalid user session. The user session has either expired or you have not logged in. Please login to AMP and refresh this page.',
						'steps' : {
							'upload_files' : ' Upload File(s)',
							'select_data_source': ' Select Data Source',
							'select_version': 'Select Version',
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
						    'msg_project_not_editable':'Projects marked with an * and highlighted are not editable because they are not in the current workspace.',
						    'similar_titles': '# of Similar Titles',
                            'iati_id_tooltip': 'IATI ID - A globally unique identifier for the project.',
                            'source_project_tooltip': 'The title of the project extracted from IATI data store or IATI file',
                            'destination_project_tooltip': 'The title of the project in the destination system that will be updated',
                            'similar_titles_tooltip': 'Click on the blue bubble to see projects with a similar title in the destination system. This also allows you to map one of the similar projects to the source project.',
                            'override_title_tooltip': 'Selecting this will replace the title of the project in the destination with a the title from the source project'                            
						},
						'similar_projects_dlg': {
							'title': 'Projects with similar title',
							'close': 'Close',
							'select_project': 'Map Selected Project',
							'iati_id': 'IATI ID',
							'project_title': 'Project Title',
						   'msg_project_not_editable':'Projects marked with an * and highlighted are not editable because they are not in the current workspace.',
							'message': 'projects in the destination system'
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
							'organization' : 'Organization Fields',
							'location': 'Location Fields',
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
							'close' : 'Close',
							'save_mapping' : 'Save Mapping',
							'save_as_copy' : 'Save as Copy',
							'mapping_exists' : 'Mapping name already exists'
						},
						'save_value_mappings_dlg' : {
							'msg_error_saving' : 'Error saving template',
							'title' : 'Save Value Mappings',
							'close' : 'Close',
							'save_mapping' : 'Save Mapping',
							'save_as_copy' : 'Save as Copy',
							'mapping_exists' : 'Mapping name already exists'
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
							'question' : 'Are you sure you want to restart the process?',
							'import_option': 'Import Option',
							'import_option_explanation': ' (Determines how existing projects are updated)',
							'import_option_overwrite_all': 'Overwrite all funding information',
							'import_option_overwrite_all_explanation': 'All funding information in the project is overwritten with data from the IATI file',
							'import_option_overwrite_prompt': 'You have selected the "Overwrite all funding information" Import option. This option updates existing activities by overwriting all fundings of the affected activities. Do you want to proceed? ',
							'import_option_add_missing': 'Only add missing information',
							'import_option_add_missing_explanation':'Only adds new funding information to the project. Existing funding information is not overwritten',
							'import_option_replace': 'Replace funding information',
							'import_option_replace_explanation': 'Replaces funding information in the AMP project with funding information from the IATI file. This is done per donor. Data for donors that are not reporting is not affected.',
							'import_option_replace_prompt': 'You have selected the "Replace funding information" Import option. This option updates existing activities by overwriting the fundings for donors that have data in the IATI file. Do you want to proceed? ',
							'process_next_version' :'Process Next Version'
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
					},
					'import_type':{
					    'select_import_type': 'Please select the import type:',
						'manual': 'Manual',
						'automatic': 'Automated',
						'next': 'Next',
						'description_manual': 'Follow the manual process if you have a valid IATI file that you want to upload.',
						'description_automatic': 'Follow the automated process if you want to search for available IATI data and pull it directly from the IATI datastore.',
						'data_updated': 'data has been updated since last sync.',
            'tooltip_manual': 'Upload your own IATI compliant XML file.',
            'tooltip_automatic': 'Select which reporting organization you wish to automatically import from IATI.'
          },
		  'data_source': {
						'title': 'Data Source Management',
						'default_url': 'Default URL',
						'add_custom_data_source': 'Add Exception',
						'save': 'Save',
						'exception_url': 'URL',
						'reporting_org': 'Reporting Organization',
						'select_reporting_org': 'Select Reporting Organization',
						'update': 'Update',
						'cancel': 'Cancel',
						'validation_default_url_required': 'Default URL is required',
						'validation_reporting_org_required': 'Reporting Organization is required',
						'actions':'Actions',
						'validation_invalid_url': 'Invalid URL',
						'select_data_source': 'Select Data Source',
						'select_data_reporting_org': 'Select the reporting organization to import from:',
						'next': 'Next',
						'error': 'Error:',
						'reporting_org_placeholder': 'Start typing the reporting organization name',
						'msg_saved_sucessfully': 'Data Source saved successfully!'
					},
					'select_version': {
						'title': 'Select Version',
						'currently_importing': 'You are about to process data for version',
						'other_versions_available': 'This publisher also has data for the following versions:',
						'import_will_repeat': 'The import process will be repeated for each version.',
						'imported': '(IMPORTED)',
						'all_versions_imported': 'All versions have been imported',
						'org_has_no_data': 'This organization does not have any IATI files for this country.',
						'processed': '(PROCESSED)'						
					},
					'project_preview':{
						'title': 'Project Preview',
						 'close': 'Close Preview',
						 'identification': 'Identification',
						 'funding_information': 'Funding Information',
						 'total_commitments':'Total Commitments', 
						 'total_disbursements': 'Total Disbursements', 
						 'total_expenditure':'Total Expenditure',
						 'participating_orgs': 'Participating Organizations',
					     'org': 'Organization',
					     'role': 'Roles',
					     'transactions': 'Transactions',
					     'transaction_date': 'Date',
					     'transaction_provider': 'Provider',
					     'transaction_type': 'Transaction Type',
					     'transaction_currency': 'Currency',
					     'transaction_value': 'Value',
					     'transaction_C': 'Commitments',
					     'transaction_D': 'Disbursements',
					     'transaction_E': 'Expenditure',
					     'planning': 'Planning',
					     'no_data': 'No Data',
					     'sectors': 'Sectors',
					     'locations': 'Locations',
					     'recipient': 'Recipient Country',
					     'policy_markers': 'Policy Markers'
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
								'admin': 'Admin',
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
									'workflow_list' : 'Lista de Flujos de Trabajo',
									'data_source' : 'Data Source Manager'

								}
							}
						}
					},
					'wizard' : {
						'next' : 'Siguiente >>',
						'previous' : '<< Anterior',
						'import_process' : 'Proceso de Importación',
						'steps' : {
							'upload_files' : ' Cargar Archivo(s)',
							'filter_data' : ' Filtrar Datos',
							'select_data_source': ' Select Data Source',
							'select_version': 'Select Version',
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
						    'msg_project_not_editable':'Los proyectos marcador por un * y resaltados no pueden ser editados puesto que no encuentran en el espacio de trabajo actual.',
						    'similar_titles': '# de títulos similares',
						    'iati_id_tooltip': 'IATI ID - A globally unique identifier for the project.',
                            'source_project_tooltip': 'The title of the project extracted from IATI data store or IATI file',
                            'destination_project_tooltip': 'The title of the project in the destination system that will be updated',
                            'similar_titles_tooltip': 'Click on the blue bubble to see projects with a similar title in the destination system. This also allows you to map one of the similar projects to the source project.',
                            'override_title_tooltip': 'Selecting this will replace the title of the project in the destination with a the title from the source project' 
						},
						'similar_projects_dlg': {
							'title': 'Proyectos con títulos similares',
							'close': 'Cerrar',
							'select_project': 'Mapear los proyectos seleccionados',
							'iati_id': 'IATI ID',
							'project_title': 'Título del proyecto',
					        'msg_project_not_editable':'Los proyectos marcador por un * y resaltados no pueden ser editados puesto que no encuentran en el espacio de trabajo actual.',
							'message': 'proyectos en el sistema destino'
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
							'organization' : 'Campos de Organizaciones',
							'location': 'Campos de localización',
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
							'close' : 'Cerrar',
							'save_mapping' : 'Guardar Mapeo',
							'save_as_copy' : 'Save as Copy',
							'mapping_exists' : 'Mapeo existente'
						},
						'save_value_mappings_dlg' : {
							'msg_error_saving' : 'Error al guardar la plantilla',
							'title' : 'Guardar los Valores de Mapeo',
							'close' : 'Cerrar',
							'save_mapping' : 'Guardar Mapeo',
							'save_as_copy' : 'Save as Copy',
							'mapping_exists' : 'Mapeo existente'
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
							'question' : 'Está seguro de reiniciar el proceso?',
							'import_option': 'Import Option',
							'import_option_explanation': ' (Determines how existing projects are updated)',
							'import_option_overwrite_all': 'Overwrite all funding information',
							'import_option_overwrite_all_explanation': 'All funding information in the project is overwritten with data from the IATI file',
							'import_option_overwrite_prompt': 'You have selected the "Overwrite all funding information" Import option. This option updates existing activities by overwriting all fundings of the affected activities. Do you want to proceed? ',
							'import_option_add_missing': 'Only add missing information',
							'import_option_add_missing_explanation':'Only adds new funding information to the project. Existing funding information is not overwritten',
							'import_option_replace': 'Replace funding information',
							'import_option_replace_explanation': 'Replaces funding information in the AMP project with funding information from the IATI file. This is done per donor. Data for donors that are not reporting is not affected.',
							'import_option_replace_prompt': 'You have selected the "Replace funding information" Import option. This option updates existing activities by overwriting the fundings for donors that have data in the IATI file. Do you want to proceed? ',
							'import_option_replace_prompt': 'You have selected the "Replace funding information" Import option. This option updates existing activities by overwriting the fundings for donors that have data in the IATI file. Do you want to proceed? ',
							'process_next_version' :'Process Next Version'
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
					},

					'import_type':{
					    'select_import_type': 'Please select the import type:',
						'manual': 'Manual',
						'automatic': 'Automated',
						'next': 'Next',
						'description_manual': 'Follow the manual process if you have a valid IATI file that you want to upload.',
						'description_automatic': 'Follow the automated process if you want to search for available IATI data and pull it directly from the IATI datastore.',
						'data_updated': 'data has been updated since last sync.'
					},
					'data_source': {
						'title': 'Data Source Management',
						'default_url': 'Default URL',
						'add_custom_data_source': 'Add Exception',
						'save': 'Save',
						'exception_url': 'URL',
						'reporting_org': 'Reporting Organization',
						'select_reporting_org': 'Select Reporting Organization',
						'update': 'Update',
						'cancel': 'Cancel',
						'validation_default_url_required': 'Default URL is required',
						'validation_reporting_org_required': 'Reporting Organization is required',
						'actions':'Actions',
						'validation_invalid_url': 'Invalid URL',
						'select_data_source': 'Select Data Source',
						'select_data_reporting_org': 'Select the reporting organization to import from:',
						'next': 'Next',
						'error': 'Error:',
						'reporting_org_placeholder': 'Start typing the reporting organization name'
					},
					'select_version': {
						'title': 'Select Version',
						'currently_importing': 'You are about to process data for version',
						'other_versions_available': 'This publisher also has data for the following versions:',
						'import_will_repeat': 'The import process will be repeated for each version.',
            'processed': '(PROCESSED)',
						'all_versions_imported': 'All versions have been imported'
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
								'admin': 'Admin',
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
									'workflow_list' : 'Flux de téléchargement',
									'data_source' : 'Data Source Manager'

								}
							}
						}
					},
					'wizard' : {
						'next' : 'Suivant >>',
						'previous' : '<< Précédent',
						'import_process' : 'Processus de Téléchargement',
						'steps' : {
							'upload_files' : ' Télécharger le(s)fichier(s)',
							'filter_data' : ' Filtrer les données',
							'select_data_source': ' Select Data Source',
							'select_version': 'Select Version',
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
							'similar_titles': 'Nbre de titres similaires',
						    'iati_id_tooltip': 'IATI ID - A globally unique identifier for the project.',
                            'source_project_tooltip': 'The title of the project extracted from IATI data store or IATI file',
                            'destination_project_tooltip': 'The title of the project in the destination system that will be updated',
                            'similar_titles_tooltip': 'Click on the blue bubble to see projects with a similar title in the destination system. This also allows you to map one of the similar projects to the source project.',
                            'override_title_tooltip': 'Selecting this will replace the title of the project in the destination with a the title from the source project' 
						},
						'similar_projects_dlg': {
							'title': 'Projets ayant un titre similaire',
							'close': 'Fermer',
							'select_project': 'Maintenir la version du projet',
							'iati_id': 'IATI ID',
							'project_title': 'Titre du projet ',
							'message' : ' projets dans le système de destination'
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
							'location': 'Champs Localisation',
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
							'save_mapping' : ' Enregistrer la correspondance ',
							'save_as_copy' : 'Sauvegarder copie',
							'mapping_exists' : 'Existe déjà'

						},
						'save_value_mappings_dlg' : {
							'msg_error_saving' : ' Erreur dans la sauvegarde du modèles ',
							'title' : ' Enregistrer la correspondence des valeurs ',
							'close' : ' Fermer ',
							'save_mapping' : ' Enregistrer la correspondance ',
							'save_as_copy' : 'Sauvegarder copie',
							'mapping_exists' : 'Existe déjà'
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
							'question' : ' Etes - vous sûr(e)de vouloir recommencer le processus de téléchargement ? ',
							'import_option': "Options d'import",
							'import_option_explanation': ' (Détermine les options de mise à jour des projets)',
							'import_option_overwrite_all': 'Ecraser toutes les informations de financement',
							'import_option_overwrite_all_explanation': "Toutes les informations de financement dans le projet sont écrasées avec les données du fichier de l'IATI",
							'import_option_overwrite_prompt': "Vous avez sélectionné l'option d'import Écraser toutes les informations de financement. Cette option met à jour les activités existantes en écrasant toutes les autres transactions financières des activités concernées. Voulez-vous poursuivre? ",
							'import_option_add_missing': 'Ajoutez uniquement les informations manquantes',
							'import_option_add_missing_explanation': "N'ajoute que les nouvelles informations de financement au projet. Les informations de financement existantes restent inchangées",
							'import_option_replace': 'Remplacer les informations de financement',
							'import_option_replace_explanation': "Remplace les informations de financement dans le projet AMP avec des informations de financement provenant du fichier IATI. Ceci est fait par donateur. Les données des donateurs ni figurant pas dans le fichier IATI ne seront pas affectées.",
							'import_option_replace_prompt': "Vous avez sélectionné l'option d'import Remplacer les informations de financement. Cette option met à jour les activités existantes en écrasant toutes les transactions financières des donateurs qui sont inclus dans le fichier de l'IATI. Voulez-vous continuer?",
							'import_option_replace_prompt': 'You have selected the "Replace funding information" Import option. This option updates existing activities by overwriting the fundings for donors that have data in the IATI file. Do you want to proceed? ',
							'process_next_version' :'Process Next Version'
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
						},
						'import_type':{
						    'select_import_type': 'Please select the import type:',
							'manual': 'Manual',
							'automatic': 'Automated',
							'next': 'Next',
							'description_manual': 'Follow the manual process if you have a valid IATI file that you want to upload.',
							'description_automatic': 'Follow the automated process if you want to search for available IATI data and pull it directly from the IATI datastore.',
							'data_updated': 'data has been updated since last sync.'
						},
						'data_source': {
							'title': 'Data Source Management',
							'default_url': 'Default URL',
							'add_custom_data_source': 'Add Exception',
							'save': 'Save',
							'exception_url': 'URL',
							'reporting_org': 'Reporting Organization',
							'select_reporting_org': 'Select Reporting Organization',
							'update': 'Update',
							'cancel': 'Cancel',
							'validation_default_url_required': 'Default URL is required',
							'validation_reporting_org_required': 'Reporting Organization is required',
							'actions':'Actions',
							'validation_invalid_url': 'Invalid URL',
							'select_data_source': 'Select Data Source',
							'select_data_reporting_org': 'Select the reporting organization to import from:',
							'next': 'Next',
							'error': 'Error:',
							'reporting_org_placeholder': 'Start typing the reporting organization name'
						},
						'select_version': {
							'title': 'Select Version',
							'currently_importing': 'You are about to process data for version',
							'other_versions_available': 'This publisher also has data for the following versions:',
							'import_will_repeat': 'The import process will be repeated for each version.',
                             'processed': '(PROCESSED)',
							'all_versions_imported': 'All versions have been imported'
						}
				}
			}

		}
};
