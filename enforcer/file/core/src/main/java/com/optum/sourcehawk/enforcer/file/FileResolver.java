package com.optum.sourcehawk.enforcer.file;

import com.optum.sourcehawk.enforcer.Resolver;

import java.io.InputStream;
import java.io.Writer;

/**
 * An interface for file resolvers to adhere to
 *
 * @author Brian Wyka
 */
public interface FileResolver extends Resolver<InputStream, Writer> {

}
