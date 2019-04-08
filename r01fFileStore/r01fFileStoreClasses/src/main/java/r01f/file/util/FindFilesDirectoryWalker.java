package r01f.file.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;
import r01f.util.types.collections.Lists;

@Slf4j
@Accessors(prefix="_")
public class FindFilesDirectoryWalker 
	 extends DirectoryWalker<File> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Path _startingPath;
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    public FindFilesDirectoryWalker(final Path startingPath,final FileFilter filter ) {
        super(filter, -1);
        _startingPath = startingPath;
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	DirectoryWalker
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void handleFile(final File file,final int depth,final Collection<File> results) throws IOException {
        log.trace("Found file: {}",file.getAbsolutePath());
        results.add(file);
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Return all files
     * @return
     */
    public List<File> getFiles() {
        List<File> files = Lists.newArrayList();

        URL url = this.getClass().getResource(_startingPath.asString());

        if (url == null) {
            log.warn("Unable to find root folder where to find files!");
            return files;
        }

        File directory = new File(url.getFile());
        
        try {
            this.walk(directory,
            		  files);
        } catch (IOException ioEx) {
            log.error("Problem finding files at {}",_startingPath,
            		  ioEx);
        }
        return files;
    }
}