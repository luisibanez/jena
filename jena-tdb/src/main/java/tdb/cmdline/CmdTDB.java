/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tdb.cmdline;

import org.apache.jena.atlas.logging.Log ;
import arq.cmdline.CmdARQ ;

import com.hp.hpl.jena.Jena ;
import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.util.Utils ;
import com.hp.hpl.jena.tdb.TDB ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.setup.DatasetBuilderStd ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.tdb.sys.TDBInternal ;

public abstract class CmdTDB extends CmdARQ
{
    protected final ModTDBDataset tdbDatasetAssembler   = new ModTDBDataset() ;

    // Check oK - remove.
//    private static final String log4Jsetup = StrUtils.strjoin("\n"
//                   , "## Plain output to stdout"
//                   , "log4j.appender.tdb.plain=org.apache.log4j.ConsoleAppender"
//                   , "log4j.appender.tdb.plain.target=System.out"
//                   , "log4j.appender.tdb.plain.layout=org.apache.log4j.PatternLayout"
//                   , "log4j.appender.tdb.plain.layout.ConversionPattern=%m%n"
//
//                   , "## Plain output with level, to stderr"
//                   , "log4j.appender.tdb.plainlevel=org.apache.log4j.ConsoleAppender"
//                   , "log4j.appender.tdb.plainlevel.target=System.err"
//                   , "log4j.appender.tdb.plainlevel.layout=org.apache.log4j.PatternLayout"
//                   , "log4j.appender.tdb.plainlevel.layout.ConversionPattern=%-5p %m%n"
//
//                   , "## Everything"
//                   , "log4j.rootLogger=INFO, tdb.plainlevel"
//
//                   , "## Loader output"
//                   , "log4j.additivity."+TDB.logLoaderName+"=false"
//                   , "log4j.logger."+TDB.logLoaderName+"=INFO, tdb.plain"
//
//                   , "## Parser output"
//                   , "log4j.additivity."+SysRIOT.riotLoggerName+"=false"
//                   , "log4j.logger."+SysRIOT.riotLoggerName+"=INFO, tdb.plainlevel "
//    ) ;
    private static boolean initialized = false ;
    
    protected CmdTDB(String[] argv)
    {
        super(argv) ;
        init() ;
        super.addModule(tdbDatasetAssembler) ;
        super.modVersion.addClass(Jena.class) ;
        super.modVersion.addClass(ARQ.class) ;
        super.modVersion.addClass(TDB.class) ;
    }
    
    public static synchronized void init()
    {
        if ( initialized )
            return ;
        // attempt once.
        initialized = true ;
        Log.setCmdLogging() ;
        TDB.init() ;
        DatasetBuilderStd.setOptimizerWarningFlag(false) ;
    }
    
    @Override
    protected void processModulesAndArgs()
    {
        super.processModulesAndArgs() ;
    }
    
    protected Location getLocation()
    {
        return tdbDatasetAssembler.getLocation() ;
    }
    
    protected DatasetGraph getDatasetGraph()
    {
        return getDataset().asDatasetGraph() ;
    }
    
    protected DatasetGraphTDB getDatasetGraphTDB()
    {
        DatasetGraph dsg = getDatasetGraph() ;
        return TDBInternal.getBaseDatasetGraphTDB(dsg) ;
    }

    protected Dataset getDataset()
    {
        return tdbDatasetAssembler.getDataset() ;
    }
    
    @Override
    protected String getCommandName()
    {
        return Utils.className(this) ;
    }
    
}
