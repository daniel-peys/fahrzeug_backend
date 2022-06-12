/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.fahrzeug.config

import com.acme.fahrzeug.config.ProfilesBanner.DEV
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import javax.persistence.Persistence.createEntityManagerFactory

/**
 * Spring Beans zur Initialisisierung der `SessionFactory` für _Hibernate Reactive_.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
 */
interface HibernateReactiveConfig {
    /**
     * Factory-Funktion zum Erzeugen von `SessionFactory` für _Hibernate Reactive_. Dabei wird META-INF/persistence.xml
     * aus dem (Windows-) Verzeichnis src\main\resources eingelesen.
     * https://hibernate.org/reactive/documentation/1.1/reference/html_single/#_obtaining_a_reactive_session_factory
     * https://itnext.io/integrating-hibernate-reactive-with-spring-5427440607fe
     *
     * @param dbProps Properties mit Praefix `db` aus z.B. `application.yml`
     * @param env Spring-Environment, um zu ermitteln, ob das Profile `dev` aktiviert ist
     * @return SessionFactory für _Hibernate Reactive_.
     */
    @Bean
    fun sessionFactory(dbProps: DbProps, env: Environment): SessionFactory {
        val emfProps = mutableMapOf(
            "javax.persistence.jdbc.url" to dbProps.url,
            "javax.persistence.jdbc.user" to dbProps.user,
            "javax.persistence.jdbc.password" to dbProps.password,
            "hibernate.dialect" to dbProps.sqlDialect,
            "hibernate.default_schema" to "fahrzeug",
        )
        addScriptProps(emfProps, dbProps.system, env)

        return createEntityManagerFactory("fahrzeugPU", emfProps)
            .unwrap(SessionFactory::class.java)
    }

    private fun addScriptProps(
        emfProps: MutableMap<String, String>,
        dbSystem: DbSystemType,
        env: Environment,
    ): Map<String, String> {
        if (!env.activeProfiles.contains(DEV)) {
            return emfProps
        }

        val logger = LoggerFactory.getLogger(HibernateReactiveConfig::class.java)
        logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        logger.warn("!   D B   w i r d   n e u   g e l a d e n   !")
        logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        val scriptDir = dbSystem.name.lowercase()
        emfProps.apply {
            put("javax.persistence.schema-generation.database.action", "drop-and-create")
            put("javax.persistence.schema-generation.create-source", "script")
            // unterhalb des Verzeichnisses src\main\resources
            put("javax.persistence.schema-generation.drop-script-source", "$scriptDir/drop.sql")
            put("javax.persistence.schema-generation.create-script-source", "$scriptDir/create.sql")
            put("javax.persistence.sql-load-script-source", "$scriptDir/insert.sql")
        }
        logger.debug("Properties fuer persistence.xml: {}", emfProps)

        return emfProps
    }

    /**
     * Initialisierung der `SessionFactory` für _Hibernate Reactive_.
     * @param factory SessionFactory für _Hibernate Reactive_, damit sie durch `persistence.xml` initialisiert wird,
     *      wobei die deklarierten SQL-Skripte aufgerufen werden.
     * @return CommandLineRunner mit der Protokollierung, dass die Initialisierung stattgefunden hat.
     */
    @Bean
    fun initSessionFactory(factory: SessionFactory) = CommandLineRunner {
        LoggerFactory.getLogger(HibernateReactiveConfig::class.java)
            .debug("SessionFactory wurde initialisiert")
    }
}
