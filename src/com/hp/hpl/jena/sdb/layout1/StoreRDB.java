/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.layout1;

import java.sql.Connection;
import java.sql.SQLException;

import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.sdb.core.sqlnode.GenerateSQL;
import com.hp.hpl.jena.sdb.layout2.TableDescTriples;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.sql.SDBExceptionSQL;

/** Store class for the Jena2 databse layout : query-only,
 *  not update via this route (use ModelRDB as normal). 
 * 
 * @author Andy Seaborne
 * @version $Id: StoreRDB.java,v 1.2 2006/04/27 21:43:48 andy_seaborne Exp $
 */

public class StoreRDB extends StoreBase1
{
    private ModelRDB model ;

    public StoreRDB(ModelRDB model)
    {
       this(model, new TableDescRDB(), new CodecRDB(model)) ;
    }    
    
    private StoreRDB(ModelRDB model, TableDescTriples triples, EncoderDecoder codec)
    {
        super(makeSDBConnection(model),
              null, // Formatter.
              null, // Loader
              new QueryCompilerFactory1(codec),
              new SQLBridgeFactory1(codec),
              new GenerateSQL(),
              triples) ;
        
        this.model = model ;
    }
    
    public static SDBConnection makeSDBConnection(ModelRDB model)
    {
        try {
           // TODO Cope with no real connection 
            Connection jdbc = model.getConnection().getConnection() ;
            return new SDBConnection(jdbc) ; 
        } catch (SQLException ex) { throw new SDBExceptionSQL("StoreRDB", ex) ; }
    }
    
    public ModelRDB getModel() { return model ; }
    
    @Override
    public long getSize() { return model.size(); }
}

/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */