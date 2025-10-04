package com.talentsboard.backend.util;

import java.util.*;

/**
 * Dictionnaire centralisé de technologies, frameworks, outils, ERP, CAO, etc.
 * Chaque mot-clé est mappé vers un libellé normalisé.
 */
public class TechDictionary {

    // Map <mot clé détecté (normalisé lowercase), libellé officiel>
    public static final Map<String, String> TECHNOLOGIES;

    static {
        Map<String, String> map = new HashMap<>();

        // ===================================================================================
        // === 1. Langages de Programmation, Scripting & Protocoles (Normalisation ++) ===
        // ===================================================================================

        map.put("java", "Java");
        map.put("python", "Python");
        map.put("bash", "Bash / Shell Scripting");
        map.put("shell", "Bash / Shell Scripting");
        map.put("powershell", "PowerShell");
        map.put("js", "JavaScript");
        map.put("javascript", "JavaScript");
        map.put("ts", "TypeScript");
        map.put("typescript", "TypeScript");
        map.put("c#", "C#");
        map.put("csharp", "C#"); // Nouvelle clé pour C#
        map.put("c sharp", "C#"); // Nouvelle clé pour C#
        map.put("c", "C");
        map.put("c++", "C++");
        map.put("cplusplus", "C++"); // Nouvelle clé pour C++
        map.put("golang", "Go / Golang");
        map.put("go", "Go / Golang");
        map.put("kotlin", "Kotlin");
        map.put("swift", "Swift");
        map.put("objective-c", "Objective-C");
        map.put("objective c", "Objective-C");
        map.put("php", "PHP");
        map.put("ruby", "Ruby");
        map.put("rust", "Rust");
        map.put("scala", "Scala");
        map.put("html", "HTML / CSS");
        map.put("css", "HTML / CSS");
        map.put("sql", "SQL");
        map.put("matlab", "MATLAB");
        map.put("r", "R (Langage statistique)");
        map.put("xml", "XML"); // Ajout
        map.put("json", "JSON"); // Ajout

        // ===================================================================================
        // === 2. Frameworks & Bibliothèques (Frontend / Backend) ===
        // ===================================================================================

        // Frontend
        map.put("react", "ReactJS");
        map.put("reactjs", "ReactJS");
        map.put("vue", "Vue.js");
        map.put("vuejs", "Vue.js");
        map.put("angular", "Angular");
        map.put("svelte", "Svelte");
        map.put("nextjs", "Next.js");
        map.put("next", "Next.js");
        map.put("nuxt", "Nuxt.js");
        map.put("redux", "Redux");
        map.put("mobx", "MobX");
        map.put("jquery", "jQuery");
        map.put("tailwind", "Tailwind CSS");
        map.put("bootstrap", "Bootstrap");
        map.put("material ui", "Material-UI / MUI");
        map.put("mui", "Material-UI / MUI");
        map.put("ant design", "Ant Design");
        map.put("axios", "Axios");

        // Backend
        map.put("spring", "Spring");
        map.put("springboot", "Spring Boot");
        map.put("spring boot", "Spring Boot");
        map.put("quarkus", "Quarkus");
        map.put("django", "Django");
        map.put("flask", "Flask");
        map.put("fastapi", "FastAPI");
        map.put("node", "Node.js");
        map.put("nodejs", "Node.js");
        map.put("express", "Express.js");
        map.put("nestjs", "NestJS");
        map.put("dotnet", ".NET / .NET Core");
        map.put(".net", ".NET / .NET Core");
        map.put("asp.net", "ASP.NET");
        map.put("asp net", "ASP.NET");
        map.put("laravel", "Laravel");
        map.put("symfony", "Symfony");
        map.put("rails", "Ruby on Rails");
        map.put("ruby on rails", "Ruby on Rails");

        // ===================================================================================
        // === 3. Cybersécurité / Pentest (Variantes avec/sans accents) ===
        // ===================================================================================

        map.put("cybersécurité", "Cybersécurité");
        map.put("cybersecurite", "Cybersécurité"); // Sans accent
        map.put("sécurité", "Cybersécurité");
        map.put("securite", "Cybersécurité"); // Sans accent
        map.put("pentest", "Pentest / Test d'intrusion");
        map.put("pentesting", "Pentest / Test d'intrusion");
        map.put("tests d'intrusion", "Pentest / Test d'intrusion");
        map.put("test d'intrusion", "Pentest / Test d'intrusion");
        map.put("osint", "OSINT (Open-Source Intelligence)");
        map.put("audits de sécurité", "Audit de Sécurité");
        map.put("audit de sécurité", "Audit de Sécurité");
        map.put("audit de securite", "Audit de Sécurité"); // Sans accent
        map.put("vulnérabilités", "Gestion des vulnérabilités");
        map.put("vulnerabilites", "Gestion des vulnérabilités"); // Sans accent
        map.put("cve", "Exploitation de vulnérabilités CVE");
        map.put("phishing analysis", "Analyse d'hameçonnage (Phishing)");
        map.put("phishing", "Analyse d'hameçonnage (Phishing)");
        map.put("securite des donnees", "Sécurité des données / Confidentalité");
        map.put("rgpd", "Conformité RGPD / Sécurité des données");
        map.put("securite des systemes", "Sécurité des Systèmes d'Information");
        map.put("ssi", "Sécurité des Systèmes d'Information (SSI)");
        map.put("si", "Systèmes d'Information (SI)");
        map.put("soc", "SOC (Security Operations Center)");
        map.put("siem", "SIEM (Security Information and Event Management)");
        map.put("iso 27001", "Norme ISO 27001");
        map.put("gouvernance ssi", "Gouvernance SSI"); // Ajout de concept
        map.put("analyse de risques", "Analyse de Risques (EBIOS / MEHARI)"); // Ajout de concept

        // Outils de Sécurité
        map.put("wireshark", "Wireshark");
        map.put("nessus", "Nessus");
        map.put("nmap", "Nmap");
        map.put("metasploit", "Metasploit");
        map.put("shodan", "Shodan");
        map.put("kali", "Kali Linux");
        map.put("burp suite", "Burp Suite");
        map.put("owasp zap", "OWASP ZAP");
        map.put("snort", "Snort / Suricata (IDS/IPS)");
        map.put("suricata", "Snort / Suricata (IDS/IPS)");
        map.put("splunk", "Splunk (SIEM)");
        map.put("qradar", "QRadar (SIEM)"); // Ajout
        map.put("maltego", "Maltego (OSINT)"); // Ajout

        // ===================================================================================
        // === 4. DevOps / Cloud (Alias et Dédoublements Cloud) ===
        // ===================================================================================

        // Cloud Providers
        map.put("aws", "AWS (Amazon Web Services)");
        map.put("amazon web services", "AWS (Amazon Web Services)");
        map.put("azure", "Azure / Microsoft Azure");
        map.put("microsoft azure", "Azure / Microsoft Azure");
        map.put("gcp", "Google Cloud Platform (GCP)");
        map.put("google cloud", "Google Cloud Platform (GCP)");
        map.put("alibaba cloud", "Alibaba Cloud");
        map.put("oci", "Oracle Cloud Infrastructure (OCI)"); // Ajout d'acronyme
        map.put("oracle cloud", "Oracle Cloud Infrastructure (OCI)");
        map.put("openstack", "OpenStack");
        map.put("iaas", "Modèles de Service Cloud (IaaS)"); // Ajout modèle
        map.put("paas", "Modèles de Service Cloud (PaaS)"); // Ajout modèle
        map.put("saas", "Modèles de Service Cloud (SaaS)"); // Ajout modèle

        // Infrastructure as Code (IaC)
        map.put("terraform", "Terraform (IaC)");
        map.put("ansible", "Ansible (IaC)");
        map.put("chef", "Chef (IaC)");
        map.put("puppet", "Puppet (IaC)");
        map.put("cloudformation", "AWS CloudFormation (IaC)");
        map.put("packer", "Packer");

        // CI/CD et Outils
        map.put("jenkins", "Jenkins (CI/CD)");
        map.put("gitlab", "GitLab CI / GitLab"); // Générique plateforme
        map.put("gitlab ci", "GitLab CI");
        map.put("github actions", "GitHub Actions (CI/CD)");
        map.put("azure devops", "Azure DevOps (CI/CD)"); // Ajout
        map.put("ci/cd", "CI/CD (Intégration et Déploiement Continus)");
        map.put("ci cd", "CI/CD (Intégration et Déploiement Continus)");
        map.put("intégration continue", "CI/CD (Intégration et Déploiement Continus)");
        map.put("integration continue", "CI/CD (Intégration et Déploiement Continus)");
        map.put("deploiement continu", "CI/CD (Intégration et Déploiement Continus)");
        map.put("déploiement continu", "CI/CD (Intégration et Déploiement Continus)");

        // Conteneurisation & Orchestration
        map.put("docker", "Docker");
        map.put("kubernetes", "Kubernetes");
        map.put("k8s", "Kubernetes");
        map.put("helm", "Helm (Gestionnaire de paquets K8s)");
        map.put("openshift", "OpenShift (Distribution Kubernetes)"); // Ajout

        // Monitoring & Logging
        map.put("prometheus", "Prometheus (Monitoring)");
        map.put("grafana", "Grafana (Visualisation)");
        map.put("elk", "ELK / Elastic Stack (Loggin)");
        map.put("elastic stack", "ELK / Elastic Stack (Loggin)");
        map.put("kibana", "Kibana (Loggin)");
        map.put("logstash", "Logstash (Loggin)"); // Ajout
        map.put("loki", "Loki (Loggin)");
        map.put("datacloud", "Datacloud (Monitoring)"); // Ajout

        // ===================================================================================
        // === 5. Bases de Données & Data (Alias, Mots Composés) ===
        // ===================================================================================

        // Bases de Données
        map.put("postgresql", "PostgreSQL");
        map.put("postgres", "PostgreSQL");
        map.put("mysql", "MySQL");
        map.put("mariadb", "MariaDB");
        map.put("sql server", "SQL Server / MS SQL");
        map.put("ms sql", "SQL Server / MS SQL");
        map.put("oracle", "Oracle Database"); // Générique BDD
        map.put("mongodb", "MongoDB (NoSQL)");
        map.put("mongo", "MongoDB (NoSQL)");
        map.put("cassandra", "Cassandra (NoSQL)");
        map.put("dynamodb", "AWS DynamoDB (NoSQL)");
        map.put("redis", "Redis (Cache/In-Memory)");
        map.put("memcached", "Memcached (Cache)");
        map.put("neo4j", "Neo4j (Base de données Graphe)");

        // Big Data / Data Science / ML
        map.put("hadoop", "Hadoop (Big Data)");
        map.put("spark", "Apache Spark");
        map.put("kafka", "Apache Kafka (Streaming)");
        map.put("airflow", "Apache Airflow (Workflow)");
        map.put("databricks", "Databricks");
        map.put("snowflake", "Snowflake (Data Warehouse)");
        map.put("dwh", "Data Warehouse");
        map.put("data warehouse", "Data Warehouse");
        map.put("data lake", "Data Lake");
        map.put("etl", "ETL / ELT (Intégration)");
        map.put("elt", "ETL / ELT (Intégration)");
        map.put("machine learning", "Machine Learning (ML)");
        map.put("ml", "Machine Learning (ML)");
        map.put("deep learning", "Deep Learning (DL)");
        map.put("dl", "Deep Learning (DL)");
        map.put("ai", "Intelligence Artificielle (IA)");
        map.put("ia", "Intelligence Artificielle (IA)");
        map.put("tensorflow", "TensorFlow");
        map.put("pytorch", "PyTorch");
        map.put("scikit-learn", "Scikit-learn");
        map.put("scikit learn", "Scikit-learn");
        map.put("pandas", "Pandas (Data Analysis)");
        map.put("numpy", "NumPy (Calcul Numérique)");

        // DataViz
        map.put("tableau", "Tableau (Visualisation)");
        map.put("power bi", "Power BI (Visualisation)");
        map.put("qlikview", "QlikView / Qlik Sense (Visualisation)");

        // ===================================================================================
        // === 6. Systèmes, Réseaux & Virtualisation (Normalisation) ===
        // ===================================================================================

        // Systèmes
        map.put("linux", "Linux (Administration)");
        map.put("debian", "Debian");
        map.put("ubuntu", "Ubuntu");
        map.put("centos", "CentOS");
        map.put("red hat", "Red Hat Enterprise Linux (RHEL)");
        map.put("rhel", "Red Hat Enterprise Linux (RHEL)");
        map.put("windows server", "Windows Server");
        map.put("server", "Windows Server");
        map.put("administration systeme", "Administration Systèmes et Réseaux");
        map.put("administration système", "Administration Systèmes et Réseaux");
        map.put("sysadmin", "Administration Systèmes et Réseaux"); // Ajout
        map.put("ldap", "LDAP / Active Directory");
        map.put("active directory", "LDAP / Active Directory");

        // Réseaux
        map.put("reseaux", "Réseaux informatiques");
        map.put("réseaux", "Réseaux informatiques");
        map.put("tcp/ip", "Protocoles TCP/IP");
        map.put("cisco", "Cisco (Routing & Switching)");
        map.put("routing", "Cisco (Routing & Switching)");
        map.put("switching", "Cisco (Routing & Switching)");
        map.put("juniper", "Juniper Networks");
        map.put("firewall", "Gestion Firewall (Générique)");
        map.put("pfsense", "pfSense (Firewall)");
        map.put("fortinet", "Fortinet (Firewall)");
        map.put("palo alto", "Palo Alto Networks (Firewall)");
        map.put("vlan", "VLAN");
        map.put("dhcp", "DHCP");
        map.put("dns", "DNS");
        map.put("vpn", "VPN (Virtual Private Network)");
        map.put("multi-sous-réseaux", "Configuration multi-sous-réseaux");

        // Virtualisation
        map.put("vmware", "VMware");
        map.put("hyper-v", "Hyper-V");
        map.put("kvm", "KVM (Kernel Virtual Machine)");
        map.put("open vswitch", "Open vSwitch");
        map.put("virtualisation", "Virtualisation");
        map.put("virtualization", "Virtualisation");
        map.put("vagrant", "Vagrant");

        // ===================================================================================
        // === 7. Gestion de Projet & Méthodologies (Alias et Acronymes) ===
        // ===================================================================================

        map.put("agile", "Méthodologie Agile");
        map.put("scrum", "Scrum (Méthodologie Agile)");
        map.put("kanban", "Kanban (Méthodologie Agile)");
        map.put("xp", "Méthodologie XP / Programmation Extrême"); // Ajout
        map.put("extreme programming", "Méthodologie XP / Programmation Extrême"); // Ajout
        map.put("pmp", "PMP / Gestion de Projet");
        map.put("prince2", "PRINCE2 / Gestion de Projet"); // Ajout
        map.put("gestion de projet", "Gestion de Projet (Générique)");
        map.put("chef de projet", "Gestion de Projet (Générique)"); // Clé pour rôle

        // Outils de Gestion
        map.put("jira", "Jira (Gestion de Projet)");
        map.put("confluence", "Confluence (Documentation)");
        map.put("trello", "Trello (Gestion de Projet)");
        map.put("microsoft project", "Microsoft Project"); // Ajout

        // ===================================================================================
        // === 8. Qualité Logicielle & Testing (Dédoublement et Complémentarité) ===
        // ===================================================================================

        map.put("qa", "Assurance Qualité (QA)");
        map.put("tests fonctionnels", "Tests Fonctionnels");
        map.put("tests unitaires", "Tests Unitaires");
        map.put("tests d'intégration", "Tests d'Intégration");
        map.put("tests automatisés", "Tests Automatisés");
        map.put("tests automatises", "Tests Automatisés");
        map.put("selenium", "Selenium (Tests Automatisés)");
        map.put("cypress", "Cypress (Tests Automatisés)"); // Ajout
        map.put("jmeter", "JMeter (Tests de Performance)");
        map.put("postman", "Postman (API Testing)");
        map.put("soapui", "SoapUI (API Testing)"); // Ajout
        map.put("tmm", "TMMi / Maturation Testing"); // Ajout

        // ===================================================================================
        // === 9. Mobile, ERP, CAO/DAO (Finalisation) ===
        // ===================================================================================

        // Mobile
        map.put("ios", "Développement iOS (Mobile)");
        map.put("android", "Développement Android (Mobile)");
        map.put("flutter", "Flutter (Mobile Cross-Platform)");
        map.put("react native", "React Native (Mobile Cross-Platform)");
        map.put("xamarin", "Xamarin (Mobile Cross-Platform)");

        // ERP / CRM
        map.put("sap", "SAP");
        map.put("s4hana", "SAP S/4HANA"); // Ajout de version spécifique
        map.put("oracle erp", "Oracle ERP");
        map.put("salesforce", "Salesforce (CRM)");
        map.put("dynamics 365", "Microsoft Dynamics 365 (ERP/CRM)");
        map.put("sage", "Sage");
        map.put("cegid", "Cegid");
        map.put("lucca", "Lucca (SIRH)");
        map.put("sirh", "SIRH");

        // CAO / DAO / Design
        map.put("autocad", "AutoCAD (CAO/DAO)");
        map.put("revit", "Revit (BIM)");
        map.put("bim", "BIM (Building Information Modeling)");
        map.put("solidworks", "SolidWorks (CAO)");
        map.put("catia", "CATIA (CAO)");
        map.put("sketchup", "SketchUp");
        map.put("archicad", "ArchiCAD");
        map.put("adobe", "Adobe Creative Suite");
        map.put("photoshop", "Adobe Photoshop");
        map.put("illustrator", "Adobe Illustrator");
        map.put("figma", "Figma (Design/UX)");
        map.put("ux", "UX / UI Design"); // Acronyme seul
        map.put("ui", "UX / UI Design"); // Acronyme seul
        map.put("ux/ui", "UX / UI Design");
        map.put("design thinking", "Design Thinking");

        // Gestion du Code
        map.put("github", "GitHub");
        map.put("git", "Git (Contrôle de Version)");
        map.put("gitlab", "GitLab"); // Générique plateforme
        map.put("bitbucket", "Bitbucket");

        // Bureautique (maintenu pour exhaustivité)
        map.put("excel", "Microsoft Excel");
        map.put("word", "Microsoft Word");
        map.put("outlook", "Microsoft Outlook");
        map.put("teams", "Microsoft Teams");
        map.put("slack", "Slack");

        TECHNOLOGIES = Collections.unmodifiableMap(map);
    }

    // Bénéfices/avantages
    public static final List<String> BENEFITS_KEYWORDS = List.of(
            "télétravail", "teletravail", "mutuelle", "ticket restaurant", "tickets restaurant",
            "budget formation", "formation", "rtt", "prime", "participation", "intéressement"
    );
}