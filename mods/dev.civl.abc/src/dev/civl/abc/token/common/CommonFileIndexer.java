package dev.civl.abc.token.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import dev.civl.abc.token.IF.FileIndexer;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.sarl.util.EmptySet;

/**
 * Straightforward implementation of {@link FileIndexer}.
 * 
 * @author siegel
 */
public class CommonFileIndexer implements FileIndexer {

	/**
	 * Mapping from canonicalized {@link File}s to {@link SourceFile}s for the
	 * files maintained by this indexer. There is a 1-1 correspondence between
	 * them. Invariant: the keys in this map are all canonical, i.e., they were
	 * returned by method {@link File#getCanonicalFile()}. The File occurring in
	 * the value is not necessarily canonical, but is equivalent to its key.
	 */
	private Map<File, SourceFile> sourceFileMap = new LinkedHashMap<>();

	/**
	 * All {@link SourceFile}s maintained by this indexer, in order.
	 */
	private ArrayList<SourceFile> sourceFiles = new ArrayList<>();

	/**
	 * Map from filenames of files occurring in this indexer to the ordered list
	 * of {@link SourceFile}s with that filename.
	 */
	private Map<String, ArrayList<SourceFile>> nameMap = new LinkedHashMap<>();

	public CommonFileIndexer() {
	}

	@Override
	public int getNumSourceFiles() {
		return sourceFiles.size();
	}

	@Override
	public SourceFile getSourceFile(int index) {
		return sourceFiles.get(index);
	}

	@Override
	public SourceFile getOrAdd(File file) {
		File canonicalFile;

		try {
			canonicalFile = file.getCanonicalFile();
		} catch (IOException e) {
			// this should only happen for some very weird reason
			// and in this case we will assume the file name is canonical
			canonicalFile = file;
		}

		SourceFile result = sourceFileMap.get(canonicalFile);

		if (result == null) {
			String filename = file.getName();
			ArrayList<SourceFile> sublist = nameMap.get(filename);
			String nickname;

			if (sublist == null) {
				sublist = new ArrayList<>();
				nameMap.put(filename, sublist);
				nickname = filename;
			} else {
				nickname = filename + "<" + (sublist.size() + 1) + ">";
			}
			result = new SourceFile(file, sourceFiles.size(), nickname);
			sourceFiles.add(result);
			sourceFileMap.put(canonicalFile, result);
			sublist.add(result);
		}
		return result;
	}

	@Override
	public void print(PrintStream out) {
		printFiltered(out, new EmptySet<String>());
	}

	@Override
	public SourceFile get(File file) {
		File canonicalFile;

		try {
			canonicalFile = file.getCanonicalFile();
		} catch (IOException e) {
			// this should only happen for some very weird reason
			// and in this case we will assume the file name is canonical
			canonicalFile = file;
		}
		return sourceFileMap.get(canonicalFile);
	}

	@Override
	public ArrayList<SourceFile> getSourceFilesWithName(String name) {
		return nameMap.get(name);
	}

	@Override
	public Set<String> getFilenames() {
		return nameMap.keySet();
	}

	@Override
	public void printFiltered(PrintStream out,
			Collection<String> ignoredPrefixes) {
		for (SourceFile sourceFile : sourceFiles) {
			String path = sourceFile.getPath();
			boolean ignore = false;

			for (String prefix : ignoredPrefixes) {
				if (path.startsWith(prefix)) {
					ignore = true;
					break;
				}
			}
			if (ignore)
				continue;
			out.print(sourceFile.getNickname());
			out.print("  (" + sourceFile.getPath() + ")");
			out.println();
		}
		out.flush();
	}

}
