#!groovy

def tag = BRANCH_NAME.replaceAll(/[^a-zA-Z0-9_-]/, "-").toLowerCase()

// Record original branch or pull request for cleanup jobs
def branch = env.CHANGE_ID == null ? BRANCH_NAME : null
def pr = env.CHANGE_ID

stage('Build') {
    node {
        checkout scm

        withEnv(["PATH+MAVEN=${tool 'M339'}/bin"]) {
            try {
                sh returnStatus: true, script: 'tar -xf ../iati-node-cache.tar'
                sh "cd import-core && mvn clean package -DskipTests -DqaBuild -DbuildSource=$tag"
                sh "cd import-core/import-ui && mvn docker:build -DiatiImporterTag=$tag -DpullRequest=$pr -Dbranch=$branch -DpushImage"
            } finally {
                sh returnStatus: true,
                        script: "cd import-core/import-ui && mvn docker:removeImage -DiatiImporterTag=$tag"
                sh returnStatus: true, script: "tar -cf ../iati-node-cache.tar --remove-files" +
                        " import-core/import-ui/src/main/webapp/node" +
                        " import-core/import-ui/src/main/webapp/node_modules"
                sh returnStatus: true, script: "rm import-core/import-ui/src/main/webapp/package-lock.json"
                sh returnStatus: true, script: "cd import-core && mvn clean"
            }
        }
    }
}

stage('Deploy') {

    def deployParams

    timeout(time: 1, unit: 'HOURS') {
        deployParams = input message: 'Ready to go?', parameters: [
                string(name: 'AMP_HOST', defaultValue: 'amp-haiti-develop-tc9.ampsite.net',
                        description: 'The url of the AMP to link to. Must start with \'amp\' ' +
                                'and end with \'-tc9.ampsite.net\'.', trim: true),
                string(name: 'COUNTRY', defaultValue: 'HT', description: 'Country ISO2 Code.', trim: true),
                string(name: 'IATI_ID_FIELD', defaultValue: 'project_code', description: 'IATI ID Field.', trim: true),
                string(name: 'PROCESSOR_VERSION', defaultValue: '3x', description: 'IATI Processor Version.',
                        trim: true)]
    }

    node {
        def host = deployParams['AMP_HOST']
        def country = deployParams['COUNTRY']
        def iatiField = deployParams['IATI_ID_FIELD']
        def processorVersion = deployParams['PROCESSOR_VERSION']
        sh "ssh sulfur 'cd /opt/docker/iati-importer && ./up.sh $tag $host $country $iatiField $processorVersion'"
    }
}
