/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.services.ejb.cadastre.repository.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.common.repository.entities.AbstractEntity;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;
import org.sola.services.common.repository.entities.ChildEntityInfo;

/**
 * Entity representing the cadastre.spatial_unit_change table. This entity is used to group all
 * cadastre objects that related to a Samoa Unit Development.
 */
@Table(name = "spatial_unit_group", schema = "cadastre")
public class UnitParcelGroup extends AbstractVersionedEntity {

    private static final String UNIT_PARCELS_LIST_FIELD = "unitParcelList";
    /**
     * Parameter name used for the spatial unit id value.
     */
    public static final String QUERY_PARAMETER_SPATIALUNITID = "spatialUnitId";
    /**
     * Parameter name used for the unit parcel group id value.
     */
    public static final String QUERY_PARAMETER_UNITPARCELGROUPID = "unitParcelGroupId";
    /**
     * WHERE clause to use when selecting group by a parcel in the group
     */
    public static final String QUERY_WHERE_BYSPATIALUNITID = "EXISTS (SELECT spatial_unit_group_id "
            + " FROM cadastre.spatial_unit_in_group WHERE spatial_unit_id = #{" + QUERY_PARAMETER_SPATIALUNITID + "} "
            + " AND spatial_unit_group_id = cadastre.spatial_unit_group.id)";
    /**
     * Bulk update statement to set the level_id on spatial_unit once the UnitParcelGroup is saved.
     */
    public static final String QUERY_UPDATE_SPATIALUNITLEVEL =
            " UPDATE cadastre.spatial_unit SET level_id = (SELECT id from cadastre.level "
            + " WHERE name = 'Unit Parcels')"
            + " FROM cadastre.spatial_unit_in_group sug, cadastre.cadastre_object co "
            + " WHERE sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "} "
            + " AND sug.spatial_unit_id = cadastre.spatial_unit.id "
            + " AND co.id = sug.spatial_unit_id AND co.type_code != 'parcel' "
            + " AND cadastre.spatial_unit.level_id IS NULL ";
    @Id
    @Column
    private String id;
    @Column(name = "hierarchy_level")
    private int hierarchyLevel;
    @Column
    private String name;
    @ChildEntityList(parentIdField = "spatialUnitGroupId", childIdField = "spatialUnitId",
    manyToManyClass = SpatialUnitInGroup.class)
    private List<UnitParcel> unitParcelList;
    // Prevent updates to the underlying parcel information. 
    @ChildEntityList(parentIdField = "spatialUnitGroupId", childIdField = "spatialUnitId",
    manyToManyClass = SpatialUnitInGroup.class, readOnly = true)
    private List<UnitParcel> parcelList;

    public UnitParcelGroup() {
        super();
        hierarchyLevel = 0;
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UnitParcel> getUnitParcelList() {
        return unitParcelList;
    }

    public void setUnitParcelList(List<UnitParcel> unitParcelList) {
        this.unitParcelList = unitParcelList;
    }

    public List<UnitParcel> getParcelList() {
        return parcelList;
    }

    public void setParcelList(List<UnitParcel> parcelList) {
        this.parcelList = parcelList;
    }

    /**
     * Plugs into the entity save process to set the deleteOnApproval flag and status code on the
     * SpatialUnitInGroup many to many entity.
     *
     * @param manyToMany The SpatialUnitInGroup many to many entity created to link the UnitParcel
     * and UnitParcelGroup
     * @param child The UnitParcel being linked to the UnitParcelGroup
     * @return The updated SpatialUnitInGroup many to many entity.
     */
    @Override
    public AbstractEntity initializeManyToMany(AbstractEntity manyToMany, AbstractEntity child) {
        if (manyToMany instanceof SpatialUnitInGroup) {
            ((SpatialUnitInGroup) manyToMany).setDeleteOnApproval(((UnitParcel) child).isDeleteOnApproval());
            ((SpatialUnitInGroup) manyToMany).setStatusCode(((UnitParcel) child).getUnitParcelStatusCode());
        }
        return manyToMany;

    }

    /**
     * Plugs into the entity load process to set a custom join query for retrieving the
     * UnitParcelList. The custom query ensures the delete_on_approval flag is populated on the
     * UnitParcel child entities.
     *
     * @param childEntityClass The class of the child entity(ies) to load
     * @return The custom SQL parameters for the join if the childEntityClass is UnitParcel or null
     * otherwise.
     */
    @Override
    public Map<String, Object> getChildJoinSqlParams(ChildEntityInfo childInfo) {
        Map<String, Object> params = null;
        if (UnitParcel.class.isAssignableFrom(childInfo.getEntityClass())) {
            params = new HashMap<String, Object>();
            if (UNIT_PARCELS_LIST_FIELD.equals(childInfo.getFieldName())) {
                params.put(CommonSqlProvider.PARAM_WHERE_PART, UnitParcel.QUERY_WHERE_UNITPARCELSBYGROUP);
            } else {
                params.put(CommonSqlProvider.PARAM_WHERE_PART, UnitParcel.QUERY_WHERE_PARCELSBYGROUP);
            }
            params.put(UnitParcel.QUERY_PARAMETER_UNITPARCELGROUPID, this.getId());
        }
        return params;
    }
}
