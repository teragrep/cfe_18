package com.teragrep.cfe18;

import com.teragrep.cfe18.handlers.entities.CFE04Transforms;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CFE04TransformsMapper {

     List<CFE04Transforms> getAllTransforms(Integer version);

     CFE04Transforms addNewCFE04Transforms(
                                               Integer cfe04Id,
                                               String name,
                                               Boolean writeMeta,
                                               Boolean writeDefault,
                                               String defaultValue,
                                               String destinationKey,
                                               String regex,
                                               String format);

     List<CFE04Transforms> getCFE04TransformsById(Integer id, Integer version);

     CFE04Transforms deleteCFE04TransformsById(Integer id);
}
