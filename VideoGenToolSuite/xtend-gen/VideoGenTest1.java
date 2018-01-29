import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Test;

@SuppressWarnings("all")
public class VideoGenTest1 {
  @Test
  public void testLoadModel() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field medias is undefined for the type VideoGeneratorModel"
      + "\nThere is no context to infer the closure\'s argument types from. Consider typing the arguments or put the closures into a typed context."
      + "\nforEach cannot be resolved");
  }
  
  public void writeInFile(final String filename, final String data) {
    try {
      FileOutputStream _fileOutputStream = new FileOutputStream(filename);
      OutputStreamWriter _outputStreamWriter = new OutputStreamWriter(_fileOutputStream, "utf-8");
      final BufferedWriter buffer = new BufferedWriter(_outputStreamWriter);
      try {
        buffer.write(data);
      } catch (final Throwable _t) {
        if (_t instanceof IOException) {
          final IOException e = (IOException)_t;
          throw e;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      } finally {
        buffer.flush();
        buffer.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public CharSequence ffmpegConcatenateCommand(final String mpegPlaylistFile, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/usr/local/bin/ffmpeg -y -f concat -safe 0 -i ");
    _builder.append(mpegPlaylistFile);
    _builder.append(" -c copy ");
    _builder.append(outputPath);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}
