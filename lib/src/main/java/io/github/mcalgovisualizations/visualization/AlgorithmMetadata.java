package io.github.mcalgovisualizations.visualization;

// In lib: io.github.mcalgovisualizations.visualization.AlgorithmMetadata
public record AlgorithmMetadata(
        String key,
        String displayName,
        String desc1,
        String desc2,
        String complexity,
        String iconNamespace // Use a String for the icon so lib doesn't need Minestom's Material
) {}