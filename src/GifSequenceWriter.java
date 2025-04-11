package src;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Iterator;

public class GifSequenceWriter {
    private final ImageWriter writer;
    private final ImageWriteParam param;
    private final IIOMetadata metadata;
    private final ImageOutputStream stream;

    public GifSequenceWriter(ImageOutputStream stream, int imageType, int delay, boolean loopF) throws IOException {
        this.stream = stream;
        this.writer = getWriter();
        this.param = writer.getDefaultWriteParam();

        ImageTypeSpecifier specific = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        this.metadata = writer.getDefaultImageMetadata(specific, param);

        String formatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);

        IIOMetadataNode graphNode = getNode(root, "GraphicControlExtension");
        graphNode.setAttribute("disposalMethod", "none");
        graphNode.setAttribute("userInputFlag", "FALSE");
        graphNode.setAttribute("transparentColorFlag", "FALSE");
        graphNode.setAttribute("delayTime", Integer.toString(delay / 10));
        graphNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode ensNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode appNode = new IIOMetadataNode("ApplicationExtension");

        appNode.setAttribute("applicationID", "NETSCAPE");
        appNode.setAttribute("authenticationCode", "2.0");

        int loop = loopF ? 0 : 1;
        byte[] loopBytes = new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)};
        appNode.setUserObject(loopBytes);
        ensNode.appendChild(appNode);
        root.appendChild(ensNode);

        metadata.setFromTree(formatName, root);
        writer.setOutput(stream);
        writer.prepareWriteSequence(null);
    }

    private static ImageWriter getWriter() {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("gif");
        if (!writers.hasNext()) throw new IllegalStateException("Gaada GIF");
        return writers.next();
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }

    public void writeToSequence(RenderedImage image) throws IOException {
        writer.writeToSequence(new IIOImage(image, null, metadata), param);
    }

    public void close() throws IOException {
        writer.endWriteSequence();
        stream.close();
    }
}
