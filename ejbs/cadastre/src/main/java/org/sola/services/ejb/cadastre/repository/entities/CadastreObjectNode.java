/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.cadastre.repository.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Table;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectNode extends AbstractReadOnlyEntity{

    private final static String EXTENT_DEFINITION = "SetSRID("
            + "ST_MakeBox3D(ST_Point(#{minx}, #{miny}),ST_Point(#{maxx}, #{maxy})), #{srid})";
    
    public final static String QUERY_GET_BY_RECTANGLE_FROM_PART = 
            "st_dumppoints((select co.geom_polygon from cadastre.cadastre_object co "
            + " where type_code= 'parcel' and status_code= 'current' "
            + " and ST_Intersects(co.geom_polygon, " + EXTENT_DEFINITION + ") limit 1)) t ";
    
    public final static String QUERY_GET_BY_RECTANGLE_WHERE_PART = 
            " ST_Intersects(t.geom, " + EXTENT_DEFINITION + ") ";
    
    public final static String    QUERY_GET_BY_RECTANGLE_POTENTIAL_FROM_PART = 
            "(select distinct ST_Line_Interpolate_Point(st_intersection("
            + "st_boundary(geom_polygon)," + EXTENT_DEFINITION 
            + "), 0.5) as geom from cadastre.cadastre_object co "
            + "where type_code= 'parcel' and status_code= 'current' and ST_Intersects(geom_polygon," 
            + EXTENT_DEFINITION + ") and st_geometrytype(st_intersection(st_boundary(geom_polygon),"
            + EXTENT_DEFINITION + ")) = 'ST_LineString' limit 1) t";
    
    @Column(name = "id")
    @AccessFunctions(onSelect = "st_astext(geom)")
    private String id;
    @Column(name = "geom")
    @AccessFunctions(onSelect = "st_asewkb(geom)")
    private byte[] geom;
    private List<CadastreObject> cadastreObjectList;

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CadastreObject> getCadastreObjectList() {
        return cadastreObjectList;
    }

    public void setCadastreObjectList(List<CadastreObject> cadastreObjectList) {
        this.cadastreObjectList = cadastreObjectList;
    }
    
}
