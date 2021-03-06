/*
 * Copyright (C) 2016 - present Juergen Zimmermann, Hochschule Karlsruhe
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

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.bind.DefaultValue

/**
 * Spring-Konfiguration für Properties zu _Spring Boot_ `spring.mail.*`.
 *
 * @author Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
 *
 * @constructor Ein Objekt zu den Properties `mail.*` für den Präfix `spring`.
 * @property host Property `spring.mail.host`.
 * @property port Property `spring.mail.port`.
 */
@ConfigurationProperties(prefix = "spring.mail")
@Suppress("unused", "UseDataClass")
class MailProps(
    val host: String = "localhost",
    private val port: Int = PORT,
) {
    private companion object {
        @Suppress("UnderscoresInNumericLiterals")
        const val PORT = 5025
    }
}

/**
 * Spring-Konfiguration für Properties `mail.*`.
 *
 * @author Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
 *
 * @constructor Ein Objekt zu den Properties `from` und `sales` für den Präfix `mail`.
 * @property from Emailadresse für _from_, z.B. `Vorname Nachname <foo@test.de>`.
 * @property sales Emailadresse für den Vertrieb.
 */
@ConfigurationProperties(prefix = "app.mail")
@ConstructorBinding
@Suppress("UseDataClass")
class MailAddressProps(
    @DefaultValue("Theo Test <theo@test.de>")
    val from: String,

    @DefaultValue("Maxi Musterfrau <maxi.musterfrau@test.de>")
    val sales: String,
)
