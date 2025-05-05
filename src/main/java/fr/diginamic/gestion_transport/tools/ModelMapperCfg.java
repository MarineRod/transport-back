package fr.diginamic.gestion_transport.tools;

public class ModelMapperCfg {
    private static org.modelmapper.ModelMapper modelMapper;

    public static org.modelmapper.ModelMapper getInstance() {
        if (modelMapper == null) {
            modelMapper = new org.modelmapper.ModelMapper();
        }
        return modelMapper;
    }
}
