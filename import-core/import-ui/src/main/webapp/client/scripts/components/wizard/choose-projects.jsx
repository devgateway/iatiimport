var React = require('react');
var typeaheadimpl = require('./../../utils/typeaheadimpl');
var ChooseProjects = React.createClass({ 
componentDidMount: function() {
    var project_names = ['Conscientisation à la promotion et à la protection des droits de peuples indigènes du territoire de Kabare au Sud Kivu',
'Projet de renforcement des activités de promotion du processus démocratique dans le diocèse de Kenge',
'Promotion de l égalité des genres la résolution des conflits ethniques en milieu rural',
'Equipement du secrétariat du GSRP lot 1 et N',
'Economie Solidaire et Sécurité Alimentaire pour la lutte contre la pauvreté au Kivu (ESSAIKI)',
'Renforcement des capacités des organisations paysannes pour la sécurité alimentaire dans le centre de Bandundu (RDC)',
'LAide et sécurité alimentaire des ménages vulnérables du Kasai Oriental',
'Projet de renforcément de capacités de gestion de migration en RDC.',
'Programme de mobilisation des ressources de la communauté Congolaise de l extérieur pour le développement de la RDC',
'Appui aux pisciculteurs de Ndjili Kilambu et démarrage d une exploitation porcine communautaire à Kifua.',
'Renforcement du mécanisme de survie de 85.000 familles extrêmement vulnérables dans les provinces de l Ituri, Nord et Sud-Kivu'];

$('.autocomplete .typeahead').typeahead({
  hint: true,
  highlight: true,
  minLength: 1
},
{
  name: 'project_names',
  displayKey: 'value',
  source: typeaheadimpl(project_names)
});
  }, 
  render: function() {  
    return (
    
        <div className="panel panel-default">
                <div className="panel-heading"><strong>Choose Projects</strong></div>
                <div className="panel-body">
                            <div className="panel panel-success">
                                <div className="panel-heading">New Projects</div>
                                <div className="panel-body">
                                    <table className="table">
                                        <thead>
                                            <tr>
                                                <th>
                                                    Import
                                                </th>
                                                <th>
                                                    Source Project
                                                </th>
                                                <th>
                                                    Destination Project
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>
                                                        <input type="checkbox" aria-label="Field1"/>
                                                </td>
                                                <td>
                                                    Source Project Name 1
                                                </td>
                                                <td>
                                                    <div className="autocomplete">
                                                      <input className="typeahead" type="text" placeholder="Destination Project Name 1"/>
                                                    </div>                                                
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                        <input type="checkbox" aria-label="Field1"/>
                                                </td>
                                                <td>
                                                    Source Project Name 2
                                                </td>
                                                <td>
                                                    <div className="autocomplete">
                                                      <input className="typeahead" type="text" placeholder="Destination Project Name 2"/>
                                                    </div>                                                
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div className="panel panel-danger">
                                <div className="panel-heading">Existing Projects</div>
                                <div className="panel-body">
                                    <table className="table">
                                        <thead>
                                            <tr>
                                                <th>
                                                    Update
                                                </th>
                                                <th>
                                                    Source Project
                                                </th>
                                                <th>
                                                    Destination Project
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>
                                                    <input type="checkbox" aria-label="Field1"/>
                                                </td>
                                                <td>
                                                    Source Project Name 1
                                                </td>
                                                <td>
                                                    <div className="autocomplete">
                                                      <input className="typeahead" type="text" placeholder="Destination Project Name 1"/>
                                                    </div>                                                
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <input type="checkbox" aria-label="Field1"/>
                                                </td>
                                                <td>
                                                    Source Project Name 2
                                                </td>
                                                <td>
                                                    <div className="autocomplete">
                                                      <input className="typeahead" type="text" placeholder="Destination Project Name 2"/>
                                                    </div>                                                
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div className="buttons">
                            <button type="button" className="btn btn-success navbar-btn btn-custom" >Next >></button>
                        </div>
                
        </div>
        
    );
  }
});

module.exports = ChooseProjects;
