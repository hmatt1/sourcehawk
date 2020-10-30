package com.optum.sourcehawk.core.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * File Utilities
 *
 * @author Brian Wyka
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    /**
     * Derive the relative path for {@code absolutePath} relative to {@code root}
     *
     * @param root the root to determine relativity
     * @param absolutePath the absolute path
     * @return the path relative to the root
     */
    public static String deriveRelativePath(final String root, final String absolutePath) {
        final String prefixToRemove;
        if (root.endsWith("/")) {
            prefixToRemove = root;
        } else {
            prefixToRemove = root + "/";
        }
        return absolutePath.replace(prefixToRemove, "");
    }

    /**
     * Find file paths by a glob pattern.
     * <p>
     * If a glob pattern is not provided, then this method will return a stream of one {@link Path}
     * representation for the provided arguments (if the path exists), otherwise an empty stream.
     * <p>
     * In other words, this method will only ever return a stream of actual files which exist.
     *
     * @param root the start location to search
     * @param pathOrPattern the path or glob pattern, i.e **&#47;path/**&#47;*.txt
     * @return the collection of paths which match the pattern, or an empty collection if no matches found
     * @throws IOException if any error occurs walking the file tree attempting to match paths
     */
    public static Stream<Path> find(final String root, final String pathOrPattern) throws IOException {
        if (StringUtils.isBlankOrEmpty(root) || StringUtils.isBlankOrEmpty(pathOrPattern)) {
            return Stream.empty();
        }
        if (isGlobPattern(pathOrPattern)) {
            val pathMatcher = FileSystems.getDefault().getPathMatcher(String.format("glob:%s", pathOrPattern));
            val matchedPaths = Stream.<Path>builder();
            Files.walkFileTree(Paths.get(root), new PathMatcherFileVisitor(pathMatcher, matchedPaths));
            return matchedPaths.build();
        }
        return Stream.of(Paths.get(root).resolve(pathOrPattern)).filter(Files::exists);
    }

    /**
     * Determine if the provided pattern is a glob pattern
     *
     * @param pattern the pattern to check
     * @return true if a glob pattern, false otherwise
     */
    public static boolean isGlobPattern(final String pattern) {
        return pattern.contains("*") || pattern.contains("?") || pattern.contains("[");
    }

    /**
     * A file visitor which uses a {@link PathMatcher} to find paths that match
     *
     * @author Brian Wyka
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class PathMatcherFileVisitor extends SimpleFileVisitor<Path> {

        private final PathMatcher pathMatcher;
        private final Stream.Builder<Path> matchedPaths;

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFile(final Path path, final BasicFileAttributes basicFileAttributes) {
            if (basicFileAttributes.isDirectory()) {
                return FileVisitResult.CONTINUE;
            }
            if (pathMatcher.matches(path)) {
                matchedPaths.add(path);
            }
            return FileVisitResult.CONTINUE;
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFileFailed(final Path path, final IOException ioException) {
            return FileVisitResult.CONTINUE;
        }

    }

}
