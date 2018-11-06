/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom.providers;

import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.util.Constants;
import net.fabricmc.loom.util.DependencyProvider;
import org.gradle.api.Project;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

//TODO fix local mappings
//TODO possibly use maven for mappings, can fix above at the same time
public class PomfProvider extends DependencyProvider {

	public String minecraftVersion;
	public String pomfVersion;

	private File POMF_DIR;
	public File MAPPINGS_TINY;

	public File MAPPINGS_MIXIN_EXPORT;

	@Override
	public void provide(DependencyInfo dependency, Project project, LoomGradleExtension extension) throws Exception {
		project.getLogger().lifecycle(":setting up pomf " + dependency.getDependency().getVersion());

		String version = dependency.getDependency().getVersion();
		String[] split = version.split("\\.");

		File mappingsJar = dependency.resolveFile();

		this.minecraftVersion = split[0];
		this.pomfVersion = split[1];

		initFiles(project);

		if (!POMF_DIR.exists()) {
			POMF_DIR.mkdir();
		}


		if (!MAPPINGS_TINY.exists()) {
			project.getLogger().lifecycle(":extracting " + mappingsJar.getName());
			try (FileSystem fileSystem = FileSystems.newFileSystem(mappingsJar.toPath(), null)) {
				Path fileToExtract = fileSystem.getPath("mappings/mappings.tiny");
				Files.copy(fileToExtract, MAPPINGS_TINY.toPath());
			}
		}
	}

	public void initFiles(Project project) {
		LoomGradleExtension extension = project.getExtensions().getByType(LoomGradleExtension.class);
		POMF_DIR = new File(extension.getUserCache(), "pomf");

		MAPPINGS_TINY = new File(POMF_DIR, "pomf-tiny-" + minecraftVersion + "." + pomfVersion);
		MAPPINGS_MIXIN_EXPORT = new File(Constants.CACHE_FILES, "mixin-map-" + minecraftVersion + "." + pomfVersion + ".tiny");
	}

	@Override
	public String getTargetConfig() {
		return Constants.MAPPINGS;
	}
}