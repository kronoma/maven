package org.apache.maven.caching.xml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.caching.xml.buildinfo.BuildInfo;
import org.apache.maven.caching.xml.buildinfo.io.xpp3.CacheBuildInfoXpp3Reader;
import org.apache.maven.caching.xml.buildinfo.io.xpp3.CacheBuildInfoXpp3Writer;
import org.apache.maven.caching.xml.buildsdiff.BuildDiff;
import org.apache.maven.caching.xml.buildsdiff.io.xpp3.CacheBuildsDiffXpp3Reader;
import org.apache.maven.caching.xml.buildsdiff.io.xpp3.CacheBuildsDiffXpp3Writer;
import org.apache.maven.caching.xml.config.CacheConfig;
import org.apache.maven.caching.xml.config.io.xpp3.CacheConfigXpp3Reader;
import org.apache.maven.caching.xml.config.io.xpp3.CacheConfigXpp3Writer;
import org.apache.maven.caching.xml.report.CacheReport;
import org.apache.maven.caching.xml.report.io.xpp3.CacheReportXpp3Reader;
import org.apache.maven.caching.xml.report.io.xpp3.CacheReportXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * XmlService
 */
@Singleton
@Named
public class XmlService
{

    public byte[] toBytes( CacheConfig cache ) throws IOException
    {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() )
        {
            new CacheConfigXpp3Writer().write( baos, cache );
            return baos.toByteArray();
        }
    }

    public byte[] toBytes( BuildInfo buildInfo ) throws IOException
    {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() )
        {
            new CacheBuildInfoXpp3Writer().write( baos, buildInfo );
            return baos.toByteArray();
        }
    }

    public byte[] toBytes( BuildDiff diff ) throws IOException
    {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() )
        {
            new CacheBuildsDiffXpp3Writer().write( baos, diff );
            return baos.toByteArray();
        }
    }

    public byte[] toBytes( CacheReport cacheReportType ) throws IOException
    {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() )
        {
            new CacheReportXpp3Writer().write( baos, cacheReportType );
            return baos.toByteArray();
        }
    }

    public <T> T fromFile( Class<T> clazz, File file ) throws IOException, XmlPullParserException
    {
        return fromInputStream( clazz, Files.newInputStream( file.toPath() ) );
    }

    public <T> T fromBytes( Class<T> clazz, byte[] bytes )
    {
        return fromInputStream( clazz, new ByteArrayInputStream( bytes ) );
    }

    public <T> T fromInputStream( Class<T> clazz, InputStream inputStream )
    {
        try
        {
            if ( clazz == BuildInfo.class )
            {
                return clazz.cast( new CacheBuildInfoXpp3Reader().read( inputStream ) );
            }
            else if ( clazz == CacheConfig.class )
            {
                return clazz.cast( new CacheConfigXpp3Reader().read( inputStream ) );
            }
            else if ( clazz == BuildDiff.class )
            {
                return clazz.cast( new CacheBuildsDiffXpp3Reader().read( inputStream ) );
            }
            else if ( clazz == CacheReport.class )
            {
                return clazz.cast( new CacheReportXpp3Reader().read( inputStream ) );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported type " + clazz );
            }
        }
        catch ( IOException | XmlPullParserException e )
        {
            throw new RuntimeException( "Unable to parse cache xml element", e );
        }
    }
}