package com.ehealth.dermassist.data.fhir

import com.ehealth.dermassist.data.model.ScanEntity
import com.ehealth.dermassist.domain.model.User
import java.util.*
import org.hl7.fhir.r4.model.*

/**
 * Mapper utility to convert internal DermAssist domain models to HL7 FHIR R4 resources. This
 * ensures the application data can be interoperable with other health systems.
 */
object FhirMapper {

    /** Converts a DermAssist User to a FHIR Patient resource. */
    fun toFhirPatient(user: User): Patient {
        val patient = Patient()
        patient.id = user.id

        // Name
        val name = patient.addName()
        name.family = user.name.substringAfterLast(" ", "")
        name.addGiven(user.name.substringBeforeLast(" ", user.name))

        // Contact
        patient
            .addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
            .setValue(user.email)
            .setUse(ContactPoint.ContactPointUse.HOME)

        // Age/BirthDate approximation (simplified for this demo)
        user.age?.let {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -it)
            patient.birthDate = calendar.time
        }

        return patient
    }

    /**
     * Converts a ScanEntity to a FHIR Observation resource. Uses LOINC codes where applicable for
     * skin assessment.
     */
    fun toFhirObservation(scan: ScanEntity, patientId: String): Observation {
        val observation = Observation()
        observation.id = scan.id
        observation.status = Observation.ObservationStatus.FINAL

        // Link to Patient
        observation.subject = Reference("Patient/$patientId")

        // Code for Skin Assessment (LOINC 86665-7: Skin assessment)
        observation.code
            .addCoding()
            .setSystem("http://loinc.org")
            .setCode("86665-7")
            .setDisplay("Skin assessment")

        observation.effective = DateTimeType(Date(scan.createdAt))

        // Body Site
        if (scan.scanArea.isNotBlank()) {
            observation.bodySite.text = scan.scanArea
        }

        // Overall Score as a component or value
        observation.value =
            Quantity().apply {
                value = scan.overallScore.toBigDecimal()
                unit = "Score"
                system = "http://unitsofmeasure.org"
                code = "1"
            }

        // Metrics as Components
        scan.metrics.forEach { metric ->
            val component = observation.addComponent()
            component.code.addCoding().setDisplay(metric.name)
            component.value =
                Quantity().apply {
                    value = metric.value.toBigDecimal()
                    unit = "%"
                    system = "http://unitsofmeasure.org"
                    code = "%"
                }
        }

        // Conditions as Interpretations
        scan.conditions.forEach { condition ->
            observation
                .addInterpretation()
                .addCoding()
                .setSystem("http://snomed.info/sct") // Simplified SNOMED placeholder
                .setDisplay(condition.label)
        }

        // Image URL as an extension or related document
        if (scan.imageUrl.isNotBlank()) {
            val extension = observation.addExtension()
            extension.url = "http://ehealth.com/fhir/StructureDefinition/skin-image-url"
            extension.setValue(StringType(scan.imageUrl))
        }

        return observation
    }
}
