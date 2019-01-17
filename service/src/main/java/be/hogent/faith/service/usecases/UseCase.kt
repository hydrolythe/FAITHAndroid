package be.hogent.faith.service.usecases

interface UseCase{
    //TODO: define a more complete interface for the different kind of streams that can be returned
    //Possible each UseCase should get a wrapper object for the input it requires, and a generic for the output
    fun execute()
}