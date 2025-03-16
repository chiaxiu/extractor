#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-compiler:2.0.21")
@file:DependsOn("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.21")

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import java.io.File

fun extractPublicDeclarations(sourceDir: File, configFiles: EnvironmentConfigFiles) {
    val kotlinFiles = sourceDir.walk()
        .filter { it.extension == "kt" }
        .map { it.canonicalPath }
        .toList()

    if (kotlinFiles.isEmpty()) {
        println("No Kotlin files found in the provided directory.")
        return
    }

    val configuration = CompilerConfiguration().apply {
        addKotlinSourceRoots(kotlinFiles)
        put(JVMConfigurationKeys.DO_NOT_CLEAR_BINDING_CONTEXT, true)
        put(CommonConfigurationKeys.MODULE_NAME, "PublicDeclarationExtractor")
    }

    val disposable: Disposable = Disposer.newDisposable()

    try {
        val environment = KotlinCoreEnvironment.createForProduction(disposable, configuration, configFiles)
        val psiFiles = environment.getSourceFiles()
        for (file in psiFiles) {
            processKtFile(file)
        }
    } finally {
        Disposer.dispose(disposable)
    }
}

fun processKtFile(ktFile: KtFile) {
    ktFile.declarations.forEach { declaration -> printDeclaration(declaration) }
}

fun printDeclaration(declaration: KtDeclaration, indent: String = "") {
    if (declaration.isPublic) {
        when (declaration) {
            is KtNamedFunction -> println("${indent}fun ${declaration.name}()")
            is KtClass -> {
                println("${indent}class ${declaration.name} {")
                declaration.declarations.forEach { inner ->
                    printDeclaration(inner, "$indent    ")
                }
                println("$indent}")
            }
            is KtProperty -> println("${indent}val/var ${declaration.name}")
            is KtObjectDeclaration -> {
                println("${indent}object ${declaration.name} {")
                declaration.declarations.forEach { inner ->
                    printDeclaration(inner, "$indent    ")
                }
                println("$indent}")
            }
        }
    }
}

if (args.isEmpty()) {
    println("Usage: <script> <source_directory>")
} else {
    val sourceDir = File(args[0])
    if (!sourceDir.exists() || !sourceDir.isDirectory) {
        println("Invalid directory: ${args[0]}")
    } else {
        val environmentConfigFiles = EnvironmentConfigFiles.JVM_CONFIG_FILES
        extractPublicDeclarations(sourceDir, environmentConfigFiles)
    }
}

